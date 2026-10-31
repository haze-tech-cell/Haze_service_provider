package com.haze.servicesprovider.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.domain.model.ServiceCategory
import com.haze.servicesprovider.domain.model.UserProfile
import com.haze.servicesprovider.domain.usecase.GetCategoriesUseCase
import com.haze.servicesprovider.domain.usecase.SaveProfileUseCase
import com.haze.servicesprovider.presentation.common.Resource
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistrationFormState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val secondaryPhone: String = "",
    val secondaryPhoneError: String? = null,
    val city: String = "",
    val cityError: String? = null,
    val fullAddress: String = "",
    val fullAddressError: String? = null,
    val serviceCategory: String = "",
    val customServiceCategory: String = "",
    val categoryError: String? = null,
    val availableCategories: List<ServiceCategory> = emptyList(),
    val isCategorySheetOpen: Boolean = false,
    val saveProfileResource: Resource<UserProfile> = Resource.Idle
)

class BusinessRegistrationViewModel(
    private val saveProfileUseCase: SaveProfileUseCase = ServiceLocator.saveProfileUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase = ServiceLocator.getCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationFormState())
    val uiState: StateFlow<RegistrationFormState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val defaults = getCategoriesUseCase.getDefaults()
            _uiState.value = _uiState.value.copy(availableCategories = defaults)

            val remoteResult = getCategoriesUseCase()
            remoteResult.getOrNull()?.let { categories ->
                if (categories.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(availableCategories = categories)
                }
            }
        }
    }

    fun onFullNameChange(name: String) {
        _uiState.value = _uiState.value.copy(fullName = name, fullNameError = null)
    }

    fun onSecondaryPhoneChange(phone: String) {
        val filtered = phone.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(secondaryPhone = filtered, secondaryPhoneError = null)
    }

    fun onCityChange(city: String) {
        _uiState.value = _uiState.value.copy(city = city, cityError = null)
    }

    fun onAddressChange(address: String) {
        _uiState.value = _uiState.value.copy(fullAddress = address, fullAddressError = null)
    }

    fun onCategorySelect(category: ServiceCategory) {
        _uiState.value = _uiState.value.copy(
            serviceCategory = category.name,
            categoryError = null,
            isCategorySheetOpen = false
        )
    }

    fun onCustomCategoryChange(custom: String) {
        _uiState.value = _uiState.value.copy(customServiceCategory = custom, categoryError = null)
    }

    fun setCategorySheetVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isCategorySheetOpen = visible)
    }

    fun submitRegistration(primaryPhoneNumber: String, onSuccess: () -> Unit) {
        val state = _uiState.value

        val nameVal = ValidationUtils.validateFullName(state.fullName)
        val secPhoneVal = ValidationUtils.validateSecondaryMobileNumber(state.secondaryPhone, primaryPhoneNumber)
        val cityVal = ValidationUtils.validateCity(state.city)
        val addressVal = ValidationUtils.validateAddress(state.fullAddress)
        val catVal = ValidationUtils.validateCategory(state.serviceCategory, state.customServiceCategory)

        if (!nameVal.isValid || !secPhoneVal.isValid || !cityVal.isValid || !addressVal.isValid || !catVal.isValid) {
            _uiState.value = state.copy(
                fullNameError = nameVal.errorMessage,
                secondaryPhoneError = secPhoneVal.errorMessage,
                cityError = cityVal.errorMessage,
                fullAddressError = addressVal.errorMessage,
                categoryError = catVal.errorMessage
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saveProfileResource = Resource.Loading)

            val currentUserId = ServiceLocator.authRepository.getCurrentUserId()
                ?: ServiceLocator.sessionManager.getUserId()
                ?: ""

            if (currentUserId.isBlank()) {
                val errorMsg = "Session expired. Please verify your mobile number again."
                _uiState.value = _uiState.value.copy(saveProfileResource = Resource.Error(errorMsg))
                _eventFlow.emit(UiEvent.ShowSnackbar(errorMsg))
                return@launch
            }

            val newProfile = UserProfile(
                id = currentUserId,
                fullName = nameVal.cleanedValue ?: state.fullName.trim(),
                phoneNumber = primaryPhoneNumber,
                secondaryPhoneNumber = if (state.secondaryPhone.isNotBlank()) state.secondaryPhone.trim() else null,
                city = cityVal.cleanedValue ?: state.city.trim(),
                fullAddress = addressVal.cleanedValue ?: state.fullAddress.trim(),
                serviceCategory = state.serviceCategory.trim(),
                customServiceCategory = if (state.serviceCategory.equals("Other", ignoreCase = true)) {
                    state.customServiceCategory.trim()
                } else null,
                profileCompleted = true
            )

            val result = saveProfileUseCase(newProfile, isNew = true)
            result.fold(
                onSuccess = { savedProfile ->
                    _uiState.value = _uiState.value.copy(saveProfileResource = Resource.Success(savedProfile))
                    onSuccess()
                },
                onFailure = { error ->
                    val userMsg = "Failed to save profile: ${error.localizedMessage ?: "Please try again."}"
                    _uiState.value = _uiState.value.copy(saveProfileResource = Resource.Error(userMsg, error))
                    _eventFlow.emit(UiEvent.ShowSnackbar(userMsg))
                }
            )
        }
    }
}
