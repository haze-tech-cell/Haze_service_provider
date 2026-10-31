package com.haze.servicesprovider.data.remote.dto

import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.LeadStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeadDto(
    @SerialName("id")
    val id: String,
    @SerialName("provider_id")
    val providerId: String,
    @SerialName("customer_name")
    val customerName: String,
    @SerialName("customer_phone")
    val customerPhone: String,
    @SerialName("service_category")
    val serviceCategory: String,
    @SerialName("description")
    val description: String,
    @SerialName("status")
    val status: String = "new",
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    fun toDomain(): Lead {
        return Lead(
            id = id,
            providerId = providerId,
            customerName = customerName,
            customerPhone = customerPhone,
            serviceCategory = serviceCategory,
            description = description,
            status = LeadStatus.fromString(status),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
