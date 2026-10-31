package com.haze.servicesprovider.domain.repository

import com.haze.servicesprovider.domain.model.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun checkInitialAuthState(): AuthState
    suspend fun sendPhoneOtp(fullPhoneNumber: String): Result<Unit>
    suspend fun verifyPhoneOtp(fullPhoneNumber: String, token: String): Result<String>
    suspend fun resendOtp(fullPhoneNumber: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    fun getCurrentUserId(): String?
    fun getCurrentUserPhone(): String?
}
