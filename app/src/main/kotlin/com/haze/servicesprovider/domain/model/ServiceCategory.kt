package com.haze.servicesprovider.domain.model

data class ServiceCategory(
    val id: String,
    val name: String,
    val iconName: String,
    val isActive: Boolean = true,
    val displayOrder: Int = 0
)
