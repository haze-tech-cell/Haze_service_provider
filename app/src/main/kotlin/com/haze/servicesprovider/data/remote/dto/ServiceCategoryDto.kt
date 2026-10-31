package com.haze.servicesprovider.data.remote.dto

import com.haze.servicesprovider.domain.model.ServiceCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceCategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("icon_name")
    val iconName: String,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("display_order")
    val displayOrder: Int = 0
) {
    fun toDomain(): ServiceCategory {
        return ServiceCategory(
            id = id,
            name = name,
            iconName = iconName,
            isActive = isActive,
            displayOrder = displayOrder
        )
    }
}
