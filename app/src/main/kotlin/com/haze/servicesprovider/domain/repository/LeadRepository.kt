package com.haze.servicesprovider.domain.repository

import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.LeadStatus
import kotlinx.coroutines.flow.Flow

interface LeadRepository {
    fun observeLeads(providerId: String): Flow<List<Lead>>
    suspend fun getLeads(providerId: String, city: String? = null, category: String? = null): Result<List<Lead>>
    suspend fun updateLeadStatus(leadId: String, status: LeadStatus): Result<Unit>
    suspend fun sendQuotation(
        leadId: String,
        providerId: String,
        visitingCharges: Double,
        estimatedArrival: String,
        notes: String?
    ): Result<Unit>
}
