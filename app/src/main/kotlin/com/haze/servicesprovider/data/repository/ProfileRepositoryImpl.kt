package com.haze.servicesprovider.data.repository

import com.haze.servicesprovider.data.local.SessionManager
import com.haze.servicesprovider.data.remote.SupabaseClientProvider
import com.haze.servicesprovider.data.remote.dto.ProfileDto
import com.haze.servicesprovider.domain.model.UserProfile
import com.haze.servicesprovider.domain.repository.ProfileRepository
import com.haze.servicesprovider.utils.AppConfig
import com.haze.servicesprovider.utils.DateUtils
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(
    private val supabaseProvider: SupabaseClientProvider,
    private val sessionManager: SessionManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> {
        return sessionManager.cachedProfile
    }

    override suspend fun getProfile(userId: String): Result<UserProfile?> = withContext(ioDispatcher) {
        runCatching {
            val response = supabaseProvider.postgrest.from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<ProfileDto>()

            val profile = response?.toDomain()
            if (profile != null) {
                sessionManager.saveProfile(profile)
            }
            profile
        }
    }

    override suspend fun createProfile(profile: UserProfile): Result<UserProfile> = withContext(ioDispatcher) {
        runCatching {
            val nowIso = DateUtils.getCurrentIsoTimestamp()
            val profileToSave = profile.copy(
                profileCompleted = true,
                createdAt = profile.createdAt ?: nowIso,
                updatedAt = nowIso
            )
            val dto = ProfileDto.fromDomain(profileToSave)

            supabaseProvider.postgrest.from("profiles")
                .upsert(dto)

            sessionManager.saveProfile(profileToSave)
            profileToSave
        }
    }

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> = withContext(ioDispatcher) {
        runCatching {
            val nowIso = DateUtils.getCurrentIsoTimestamp()
            val profileToUpdate = profile.copy(updatedAt = nowIso)
            val dto = ProfileDto.fromDomain(profileToUpdate)

            supabaseProvider.postgrest.from("profiles")
                .update(dto) {
                    filter {
                        eq("id", profile.id)
                    }
                }

            sessionManager.saveProfile(profileToUpdate)
            profileToUpdate
        }
    }

    override suspend fun uploadProfilePhoto(
        userId: String,
        imageBytes: ByteArray,
        fileExtension: String
    ): Result<String> = withContext(ioDispatcher) {
        runCatching {
            val bucket = supabaseProvider.storage.from(AppConfig.PROFILE_STORAGE_BUCKET)
            val fileName = "$userId/avatar_${System.currentTimeMillis()}.$fileExtension"

            bucket.upload(fileName, imageBytes) {
                upsert = true
            }

            val publicUrl = bucket.publicUrl(fileName)

            // Update photo URL in current profile if exists
            val current = sessionManager.cachedProfile.value
            if (current != null) {
                val updated = current.copy(profilePhotoUrl = publicUrl)
                updateProfile(updated)
            }

            publicUrl
        }
    }

    override suspend fun clearCachedProfile() {
        sessionManager.clearSession()
    }
}
