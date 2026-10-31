package com.haze.servicesprovider.domain.repository

import com.haze.servicesprovider.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun getProfile(userId: String): Result<UserProfile?>
    suspend fun createProfile(profile: UserProfile): Result<UserProfile>
    suspend fun updateProfile(profile: UserProfile): Result<UserProfile>
    suspend fun uploadProfilePhoto(userId: String, imageBytes: ByteArray, fileExtension: String): Result<String>
    suspend fun clearCachedProfile()
}
