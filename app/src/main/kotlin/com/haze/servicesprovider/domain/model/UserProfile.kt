package com.haze.servicesprovider.domain.model

data class UserProfile(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val secondaryPhoneNumber: String? = null,
    val city: String,
    val fullAddress: String,
    val serviceCategory: String,
    val customServiceCategory: String? = null,
    val profilePhotoUrl: String? = null,
    val profileCompleted: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val displayCategory: String
        get() = if (serviceCategory?.equals("Other", ignoreCase = true) == true && !customServiceCategory.isNullOrBlank()) {
            customServiceCategory ?: "Service"
        } else {
            serviceCategory ?: "Service"
        }

    val firstName: String
        get() = fullName?.trim()?.split(" ")?.firstOrNull().orEmpty().ifBlank { "Partner" }
}
