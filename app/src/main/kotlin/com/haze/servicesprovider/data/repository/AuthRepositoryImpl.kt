package com.haze.servicesprovider.data.repository

import com.haze.servicesprovider.data.local.SessionManager
import com.haze.servicesprovider.data.remote.SupabaseClientProvider
import com.haze.servicesprovider.domain.model.AuthState
import com.haze.servicesprovider.domain.repository.AuthRepository
import com.haze.servicesprovider.domain.repository.ProfileRepository
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val supabaseProvider: SupabaseClientProvider,
    private val sessionManager: SessionManager,
    private val profileRepository: ProfileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun checkInitialAuthState(): AuthState = withContext(ioDispatcher) {
        val cachedUserId = sessionManager.getUserId()
        val cachedPhone = sessionManager.getPhoneNumber()
        val isCompleted = sessionManager.isProfileCompleted()

        if (cachedUserId.isNullOrBlank() || cachedPhone.isNullOrBlank()) {
            val state = AuthState.Unauthenticated
            _authState.value = state
            return@withContext state
        }

        if (isCompleted) {
            val cachedProfile = sessionManager.cachedProfile.value
            if (cachedProfile != null && cachedProfile.profileCompleted) {
                val state = AuthState.AuthenticatedProfileComplete(cachedProfile)
                _authState.value = state
                return@withContext state
            }
        }

        // Verify with remote profile
        try {
            val remoteProfileResult = profileRepository.getProfile(cachedUserId)
            val profile = remoteProfileResult.getOrNull()

            val state = if (profile != null && profile.profileCompleted) {
                sessionManager.saveProfile(profile)
                AuthState.AuthenticatedProfileComplete(profile)
            } else {
                AuthState.AuthenticatedProfileIncomplete(cachedUserId, cachedPhone)
            }
            _authState.value = state
            state
        } catch (_: Exception) {
            // Offline fallback to cached values
            val state = if (isCompleted) {
                val fallbackProfile = sessionManager.cachedProfile.value
                if (fallbackProfile != null) {
                    AuthState.AuthenticatedProfileComplete(fallbackProfile)
                } else {
                    AuthState.AuthenticatedProfileIncomplete(cachedUserId, cachedPhone)
                }
            } else {
                AuthState.AuthenticatedProfileIncomplete(cachedUserId, cachedPhone)
            }
            _authState.value = state
            state
        }
    }

    override suspend fun sendPhoneOtp(fullPhoneNumber: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            supabaseProvider.auth.signInWith(OTP) {
                phone = fullPhoneNumber
            }
        }
    }

    override suspend fun verifyPhoneOtp(fullPhoneNumber: String, token: String): Result<String> = withContext(ioDispatcher) {
        runCatching {
            supabaseProvider.auth.verifyPhoneOtp(
                type = OtpType.Phone.SMS,
                phone = fullPhoneNumber,
                token = token
            )

            val session = supabaseProvider.auth.currentSessionOrNull()
            val userId = session?.user?.id
                ?: throw IllegalStateException("Failed to retrieve authenticated user session")

            sessionManager.saveAuthSession(userId, fullPhoneNumber)

            // Check if profile already exists in DB
            val existingProfile = profileRepository.getProfile(userId).getOrNull()
            if (existingProfile != null && existingProfile.profileCompleted) {
                sessionManager.saveProfile(existingProfile)
                _authState.value = AuthState.AuthenticatedProfileComplete(existingProfile)
            } else {
                _authState.value = AuthState.AuthenticatedProfileIncomplete(userId, fullPhoneNumber)
            }

            userId
        }
    }

    override suspend fun resendOtp(fullPhoneNumber: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            supabaseProvider.auth.signInWith(OTP) {
                phone = fullPhoneNumber
            }
        }
    }

    override suspend fun logout(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            try {
                supabaseProvider.auth.signOut()
            } catch (_: Exception) {
                // Continue clearing local session even if remote logout fails
            }
            sessionManager.clearSession()
            profileRepository.clearCachedProfile()
            _authState.value = AuthState.Unauthenticated
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val userId = getCurrentUserId() ?: throw IllegalStateException("User not authenticated")
            // In accordance with Section 10 & 13: Authorized deletion of user profile
            // Profiles CASCADE leads, and auth user is cleaned
            try {
                supabaseProvider.postgrest.from("profiles")
                    .delete {
                        filter {
                            eq("id", userId)
                        }
                    }
                supabaseProvider.auth.signOut()
            } finally {
                sessionManager.clearSession()
                profileRepository.clearCachedProfile()
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    override fun getCurrentUserId(): String? = sessionManager.getUserId()

    override fun getCurrentUserPhone(): String? = sessionManager.getPhoneNumber()
}
