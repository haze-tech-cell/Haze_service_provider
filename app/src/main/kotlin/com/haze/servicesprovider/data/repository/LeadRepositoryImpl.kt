package com.haze.servicesprovider.data.repository

import com.haze.servicesprovider.data.remote.SupabaseClientProvider
import com.haze.servicesprovider.data.remote.dto.LeadDto
import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.LeadStatus
import com.haze.servicesprovider.domain.repository.LeadRepository
import com.haze.servicesprovider.utils.DateUtils
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class LeadRepositoryImpl(
    private val supabaseProvider: SupabaseClientProvider,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LeadRepository {

    private val _leadsFlow = MutableStateFlow<List<Lead>>(emptyList())

    override fun observeLeads(providerId: String): Flow<List<Lead>> = _leadsFlow.asStateFlow()

    override suspend fun getLeads(
        providerId: String,
        city: String?,
        category: String?
    ): Result<List<Lead>> = withContext(ioDispatcher) {
        runCatching {
            val dtoList = supabaseProvider.postgrest.from("leads")
                .select {
                    order(column = "created_at", order = Order.DESCENDING)
                }
                .decodeList<LeadDto>()

            val domainLeads = dtoList.map { it.toDomain() }.filter { lead ->
                val isMyLead = lead.providerId == providerId
                val isBroadcastForMyCity = lead.providerId == null && (
                    city.isNullOrBlank() || lead.city.contains(city, ignoreCase = true) || city.contains(lead.city, ignoreCase = true)
                ) && (
                    category.isNullOrBlank() || lead.serviceCategory.contains(category, ignoreCase = true) || category.contains(lead.serviceCategory, ignoreCase = true)
                )
                isMyLead || isBroadcastForMyCity
            }

            _leadsFlow.value = domainLeads
            domainLeads
        }
    }

    override suspend fun updateLeadStatus(leadId: String, status: LeadStatus): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val nowIso = DateUtils.getCurrentIsoTimestamp()
            val updatePayload = buildJsonObject {
                put("status", status.value)
                put("updated_at", nowIso)
            }

            supabaseProvider.postgrest.from("leads")
                .update(updatePayload) {
                    filter {
                        eq("id", leadId)
                    }
                }

            val currentList = _leadsFlow.value
            _leadsFlow.value = currentList.map { lead ->
                if (lead.id == leadId) lead.copy(status = status, updatedAt = nowIso) else lead
            }
        }
    }

    override suspend fun sendQuotation(
        leadId: String,
        providerId: String,
        visitingCharges: Double,
        estimatedArrival: String,
        notes: String?
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val nowIso = DateUtils.getCurrentIsoTimestamp()
            val updatePayload = buildJsonObject {
                put("provider_id", providerId)
                put("status", LeadStatus.QUOTED.value)
                put("visiting_charges", visitingCharges)
                put("quoted_amount", visitingCharges)
                put("estimated_arrival", estimatedArrival)
                if (!notes.isNullOrBlank()) {
                    put("provider_notes", notes)
                }
                put("updated_at", nowIso)
            }

            supabaseProvider.postgrest.from("leads")
                .update(updatePayload) {
                    filter {
                        eq("id", leadId)
                    }
                }

            val currentList = _leadsFlow.value
            _leadsFlow.value = currentList.map { lead ->
                if (lead.id == leadId) {
                    lead.copy(
                        providerId = providerId,
                        status = LeadStatus.QUOTED,
                        visitingCharges = visitingCharges,
                        quotedAmount = visitingCharges,
                        estimatedArrival = estimatedArrival,
                        providerNotes = notes,
                        updatedAt = nowIso
                    )
                } else lead
            }
        }
    }
}
