package com.haze.servicesprovider.domain.repository

import com.haze.servicesprovider.domain.model.ServiceCategory

interface CategoryRepository {
    suspend fun getCategories(): Result<List<ServiceCategory>>
    fun getDefaultCategories(): List<ServiceCategory>
}
