package com.haze.servicesprovider.domain.model

enum class LeadStatus(val value: String, val displayName: String) {
    NEW("new", "New"),
    ACCEPTED("accepted", "Accepted"),
    REJECTED("rejected", "Rejected"),
    COMPLETED("completed", "Completed");

    companion object {
        fun fromString(status: String?): LeadStatus {
            return entries.firstOrNull { it.value.equals(status, ignoreCase = true) } ?: NEW
        }
    }
}

data class Lead(
    val id: String,
    val providerId: String,
    val customerName: String,
    val customerPhone: String,
    val serviceCategory: String,
    val description: String,
    val status: LeadStatus,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
