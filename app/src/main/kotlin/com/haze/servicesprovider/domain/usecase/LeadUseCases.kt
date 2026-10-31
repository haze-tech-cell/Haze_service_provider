package com.haze.servicesprovider.domain.usecase

import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.LeadStatus
import com.haze.servicesprovider.domain.repository.LeadRepository
import kotlinx.coroutines.flow.Flow

class GetLeadsUseCase(private val leadRepository: LeadRepository) {
    fun observe(providerId: String): Flow<List<Lead>> = leadRepository.observeLeads(providerId)

    suspend operator fun invoke(providerId: String): Result<List<Lead>> {
        return leadRepository.getLeads(providerId)
    }
}

class UpdateLeadStatusUseCase(private val leadRepository: LeadRepository) {
    suspend operator fun invoke(leadId: String, status: LeadStatus): Result<Unit> {
        return leadRepository.updateLeadStatus(leadId, status)
    }
}
