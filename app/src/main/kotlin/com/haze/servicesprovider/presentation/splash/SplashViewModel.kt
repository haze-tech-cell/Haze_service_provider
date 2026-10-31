package com.haze.servicesprovider.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.domain.model.AuthState
import com.haze.servicesprovider.domain.usecase.CheckAuthStateUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val checkAuthStateUseCase: CheckAuthStateUseCase = ServiceLocator.checkAuthStateUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        checkAppSession()
    }

    fun checkAppSession() {
        viewModelScope.launch {
            // Small branding delay for smooth visual experience
            delay(1200)
            val authState = checkAuthStateUseCase()
            _destination.value = when (authState) {
                is AuthState.AuthenticatedProfileComplete -> SplashDestination.Home
                is AuthState.AuthenticatedProfileIncomplete -> SplashDestination.CompleteRegistration(
                    authState.phoneNumber
                )
                AuthState.Unauthenticated, AuthState.Loading -> SplashDestination.MobileRegistration
            }
        }
    }
}

sealed interface SplashDestination {
    data object Home : SplashDestination
    data class CompleteRegistration(val phoneNumber: String) : SplashDestination
    data object MobileRegistration : SplashDestination
}
