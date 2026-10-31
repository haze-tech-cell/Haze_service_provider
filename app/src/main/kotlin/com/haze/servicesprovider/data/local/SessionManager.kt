package com.haze.servicesprovider.data.local

import android.content.Context
import android.content.SharedPreferences
import com.haze.servicesprovider.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val _cachedProfile = MutableStateFlow<UserProfile?>(loadProfile())
    val cachedProfile: StateFlow<UserProfile?> = _cachedProfile.asStateFlow()

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getPhoneNumber(): String? = prefs.getString(KEY_PHONE_NUMBER, null)

    fun isProfileCompleted(): Boolean = prefs.getBoolean(KEY_PROFILE_COMPLETED, false)

    fun saveAuthSession(userId: String, phoneNumber: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_PHONE_NUMBER, phoneNumber)
            .apply()
    }

    fun saveProfile(profile: UserProfile) {
        val jsonString = json.encodeToString(
            com.haze.servicesprovider.data.remote.dto.ProfileDto.serializer(),
            com.haze.servicesprovider.data.remote.dto.ProfileDto.fromDomain(profile)
        )
        prefs.edit()
            .putString(KEY_USER_ID, profile.id)
            .putString(KEY_PHONE_NUMBER, profile.phoneNumber)
            .putBoolean(KEY_PROFILE_COMPLETED, profile.profileCompleted)
            .putString(KEY_CACHED_PROFILE, jsonString)
            .apply()

        _cachedProfile.value = profile
    }

    private fun loadProfile(): UserProfile? {
        val jsonString = prefs.getString(KEY_CACHED_PROFILE, null) ?: return null
        return try {
            val dto = json.decodeFromString(
                com.haze.servicesprovider.data.remote.dto.ProfileDto.serializer(),
                jsonString
            )
            dto.toDomain()
        } catch (_: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _cachedProfile.value = null
    }

    companion object {
        private const val PREFS_NAME = "haze_provider_secure_session"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_PHONE_NUMBER = "key_phone_number"
        private const val KEY_PROFILE_COMPLETED = "key_profile_completed"
        private const val KEY_CACHED_PROFILE = "key_cached_profile"
    }
}
