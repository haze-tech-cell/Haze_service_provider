package com.haze.servicesprovider.domain.usecase

import com.haze.servicesprovider.domain.model.UserProfile
import com.haze.servicesprovider.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(private val profileRepository: ProfileRepository) {
    fun observe(): Flow<UserProfile?> = profileRepository.observeProfile()

    suspend operator fun invoke(userId: String): Result<UserProfile?> {
        return profileRepository.getProfile(userId)
    }
}

class SaveProfileUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(profile: UserProfile, isNew: Boolean): Result<UserProfile> {
        return if (isNew) {
            profileRepository.createProfile(profile)
        } else {
            profileRepository.updateProfile(profile)
        }
    }
}

class UploadProfilePhotoUseCase(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(userId: String, bytes: ByteArray, extension: String = "jpg"): Result<String> {
        return profileRepository.uploadProfilePhoto(userId, bytes, extension)
    }
}
