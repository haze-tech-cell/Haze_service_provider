package com.haze.servicesprovider.domain.model

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class AuthenticatedProfileIncomplete(val userId: String, val phoneNumber: String) : AuthState
    data class AuthenticatedProfileComplete(val profile: UserProfile) : AuthState
}
