package com.haze.servicesprovider.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.domain.model.UserProfile
import com.haze.servicesprovider.domain.usecase.DeleteAccountUseCase
import com.haze.servicesprovider.domain.usecase.GetProfileUseCase
import com.haze.servicesprovider.domain.usecase.LogoutUseCase
import com.haze.servicesprovider.presentation.common.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val profile: UserProfile? = null,
    val isLoggingOut: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    // Notification preferences
    val newLeadsNotification: Boolean = true,
    val promotionalNotification: Boolean = false,
    val smsAlerts: Boolean = true
)

class SettingsViewModel(
    private val getProfileUseCase: GetProfileUseCase = ServiceLocator.getProfileUseCase,
    private val logoutUseCase: LogoutUseCase = ServiceLocator.logoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase = ServiceLocator.deleteAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            getProfileUseCase.observe().collect { profile ->
                _uiState.value = _uiState.value.copy(profile = profile)
            }
        }
    }

    fun showLogoutDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showLogoutDialog = show)
    }

    fun showDeleteDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDeleteDialog = show)
    }

    fun toggleNewLeads(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(newLeadsNotification = enabled)
    }

    fun togglePromotions(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(promotionalNotification = enabled)
    }

    fun toggleSms(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(smsAlerts = enabled)
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoggingOut = true, showLogoutDialog = false)
            val result = logoutUseCase()
            _uiState.value = _uiState.value.copy(isLoggingOut = false)
            result.fold(
                onSuccess = { onLoggedOut() },
                onFailure = { error ->
                    _eventFlow.emit(UiEvent.ShowSnackbar("Logout failed: ${error.localizedMessage}"))
                }
            )
        }
    }

    fun deleteAccount(onAccountDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingAccount = true, showDeleteDialog = false)
            val result = deleteAccountUseCase()
            _uiState.value = _uiState.value.copy(isDeletingAccount = false)
            result.fold(
                onSuccess = { onAccountDeleted() },
                onFailure = { error ->
                    _eventFlow.emit(UiEvent.ShowSnackbar("Account deletion failed: ${error.localizedMessage}"))
                }
            )
        }
    }
}
