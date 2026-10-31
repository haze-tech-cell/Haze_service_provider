package com.haze.servicesprovider.data.remote.dto

import com.haze.servicesprovider.domain.model.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id")
    val id: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("secondary_phone_number")
    val secondaryPhoneNumber: String? = null,
    @SerialName("city")
    val city: String,
    @SerialName("full_address")
    val fullAddress: String,
    @SerialName("service_category")
    val serviceCategory: String,
    @SerialName("custom_service_category")
    val customServiceCategory: String? = null,
    @SerialName("profile_photo_url")
    val profilePhotoUrl: String? = null,
    @SerialName("profile_completed")
    val profileCompleted: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            id = id,
            fullName = fullName,
            phoneNumber = phoneNumber,
            secondaryPhoneNumber = secondaryPhoneNumber,
            city = city,
            fullAddress = fullAddress,
            serviceCategory = serviceCategory,
            customServiceCategory = customServiceCategory,
            profilePhotoUrl = profilePhotoUrl,
            profileCompleted = profileCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(profile: UserProfile): ProfileDto {
            return ProfileDto(
                id = profile.id,
                fullName = profile.fullName,
                phoneNumber = profile.phoneNumber,
                secondaryPhoneNumber = profile.secondaryPhoneNumber,
                city = profile.city,
                fullAddress = profile.fullAddress,
                serviceCategory = profile.serviceCategory,
                customServiceCategory = profile.customServiceCategory,
                profilePhotoUrl = profile.profilePhotoUrl,
                profileCompleted = profile.profileCompleted,
                createdAt = profile.createdAt,
                updatedAt = profile.updatedAt
            )
        }
    }
}
