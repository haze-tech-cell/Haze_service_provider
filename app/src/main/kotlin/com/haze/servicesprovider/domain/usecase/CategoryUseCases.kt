package com.haze.servicesprovider.domain.usecase

import com.haze.servicesprovider.domain.model.ServiceCategory
import com.haze.servicesprovider.domain.repository.CategoryRepository

class GetCategoriesUseCase(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(): Result<List<ServiceCategory>> {
        return categoryRepository.getCategories()
    }

    fun getDefaults(): List<ServiceCategory> {
        return categoryRepository.getDefaultCategories()
    }
}
