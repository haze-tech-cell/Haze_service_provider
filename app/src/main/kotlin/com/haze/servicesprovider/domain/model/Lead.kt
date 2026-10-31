package com.haze.servicesprovider.domain.model

enum class LeadStatus(val value: String, val displayName: String) {
    NEW("new", "New"),
    QUOTED("quoted", "Quoted"),
    ACCEPTED("accepted", "Accepted"),
    REJECTED("rejected", "Rejected"),
    COMPLETED("completed", "Completed"),
    CANCELLED("cancelled", "Cancelled");

    companion object {
        fun fromString(status: String?): LeadStatus {
            return entries.firstOrNull { it.value.equals(status, ignoreCase = true) } ?: NEW
        }
    }
}

data class Lead(
    val id: String,
    val providerId: String? = null,
    val customerName: String,
    val customerPhone: String,
    val serviceCategory: String,
    val description: String,
    val status: LeadStatus,
    val city: String = "Bhopal",
    val customerId: String? = null,
    val customerAddress: String? = null,
    val visitingCharges: Double = 0.0,
    val quotedAmount: Double = 0.0,
    val estimatedArrival: String? = null,
    val providerNotes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
