package com.haze.servicesprovider.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.ServiceCategory
import com.haze.servicesprovider.domain.model.UserProfile
import com.haze.servicesprovider.domain.usecase.GetCategoriesUseCase
import com.haze.servicesprovider.domain.usecase.GetLeadsUseCase
import com.haze.servicesprovider.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val profile: UserProfile? = null,
    val categories: List<ServiceCategory> = emptyList(),
    val filteredCategories: List<ServiceCategory> = emptyList(),
    val searchQuery: String = "",
    val recentLeads: List<Lead> = emptyList(),
    val isLoading: Boolean = false,
    val unreadNotificationsCount: Int = 0
)

class HomeViewModel(
    private val getProfileUseCase: GetProfileUseCase = ServiceLocator.getProfileUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase = ServiceLocator.getCategoriesUseCase,
    private val getLeadsUseCase: GetLeadsUseCase = ServiceLocator.getLeadsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
        loadHomeData()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            getProfileUseCase.observe().collect { profile ->
                _uiState.value = _uiState.value.copy(profile = profile)
                if (profile != null) {
                    loadLeads(profile.id)
                }
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load Categories
            val defaultCats = getCategoriesUseCase.getDefaults()
            _uiState.value = _uiState.value.copy(
                categories = defaultCats,
                filteredCategories = defaultCats
            )

            val remoteCats = getCategoriesUseCase()
            remoteCats.getOrNull()?.let { list ->
                if (list.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        categories = list,
                        filteredCategories = filterCategories(list, _uiState.value.searchQuery)
                    )
                }
            }

            // Load Profile if userId exists
            val userId = ServiceLocator.authRepository.getCurrentUserId()
            if (!userId.isNullOrBlank()) {
                getProfileUseCase(userId)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun loadLeads(providerId: String) {
        viewModelScope.launch {
            val profile = _uiState.value.profile
            val leadsResult = getLeadsUseCase(providerId, profile?.city, profile?.serviceCategory)
            val list = leadsResult.getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(recentLeads = list.take(3))
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredCategories = filterCategories(_uiState.value.categories, query)
        )
    }

    private fun filterCategories(all: List<ServiceCategory>, query: String): List<ServiceCategory> {
        return if (query.isBlank()) {
            all
        } else {
            all.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}
