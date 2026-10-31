package com.haze.servicesprovider.presentation.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.LeadStatus
import com.haze.servicesprovider.domain.usecase.GetLeadsUseCase
import com.haze.servicesprovider.domain.usecase.GetProfileUseCase
import com.haze.servicesprovider.domain.usecase.SendQuotationUseCase
import com.haze.servicesprovider.domain.usecase.UpdateLeadStatusUseCase
import com.haze.servicesprovider.presentation.common.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeadsUiState(
    val allLeads: List<Lead> = emptyList(),
    val filteredLeads: List<Lead> = emptyList(),
    val selectedFilter: LeadFilter = LeadFilter.ALL,
    val isLoading: Boolean = false,
    val selectedLeadForDetail: Lead? = null
)

enum class LeadFilter(val displayName: String) {
    ALL("All"),
    NEW("New"),
    QUOTED("Quoted"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    COMPLETED("Completed")
}

class LeadsViewModel(
    private val getLeadsUseCase: GetLeadsUseCase = ServiceLocator.getLeadsUseCase,
    private val updateLeadStatusUseCase: UpdateLeadStatusUseCase = ServiceLocator.updateLeadStatusUseCase,
    private val sendQuotationUseCase: SendQuotationUseCase = ServiceLocator.sendQuotationUseCase,
    private val getProfileUseCase: GetProfileUseCase = ServiceLocator.getProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeadsUiState(isLoading = true))
    val uiState: StateFlow<LeadsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    private var currentProviderId: String? = null
    private var providerCity: String? = null
    private var providerCategory: String? = null

    init {
        currentProviderId = ServiceLocator.authRepository.getCurrentUserId()
        loadProviderProfileAndLeads()
    }

    private fun loadProviderProfileAndLeads() {
        val providerId = currentProviderId ?: ServiceLocator.authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            val profileResult = getProfileUseCase(providerId)
            profileResult.onSuccess { profile ->
                providerCity = profile?.city
                providerCategory = profile?.serviceCategory
            }
            loadLeads()
        }
    }

    fun loadLeads() {
        val providerId = currentProviderId ?: ServiceLocator.authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = getLeadsUseCase(providerId, providerCity, providerCategory)
            result.fold(
                onSuccess = { leads ->
                    _uiState.value = _uiState.value.copy(
                        allLeads = leads,
                        filteredLeads = filterLeads(leads, _uiState.value.selectedFilter),
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Failed to fetch leads: ${error.localizedMessage}"))
                }
            )
        }
    }

    fun onFilterSelected(filter: LeadFilter) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredLeads = filterLeads(_uiState.value.allLeads, filter)
        )
    }

    fun onLeadSelected(lead: Lead?) {
        _uiState.value = _uiState.value.copy(selectedLeadForDetail = lead)
    }

    fun sendQuotation(leadId: String, visitingCharges: Double, estimatedArrival: String, notes: String?) {
        val providerId = currentProviderId ?: ServiceLocator.authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            val result = sendQuotationUseCase(
                leadId = leadId,
                providerId = providerId,
                visitingCharges = visitingCharges,
                estimatedArrival = estimatedArrival,
                notes = notes
            )
            result.fold(
                onSuccess = {
                    val updatedList = _uiState.value.allLeads.map { lead ->
                        if (lead.id == leadId) {
                            lead.copy(
                                providerId = providerId,
                                status = LeadStatus.QUOTED,
                                visitingCharges = visitingCharges,
                                quotedAmount = visitingCharges,
                                estimatedArrival = estimatedArrival,
                                providerNotes = notes
                            )
                        } else lead
                    }
                    _uiState.value = _uiState.value.copy(
                        allLeads = updatedList,
                        filteredLeads = filterLeads(updatedList, _uiState.value.selectedFilter),
                        selectedLeadForDetail = null
                    )
                    _eventFlow.emit(UiEvent.ShowSnackbar("Quotation of ₹${visitingCharges.toInt()} sent to customer!"))
                },
                onFailure = { error ->
                    _eventFlow.emit(UiEvent.ShowSnackbar("Failed to send quote: ${error.localizedMessage}"))
                }
            )
        }
    }

    fun acceptLead(leadId: String) {
        updateStatus(leadId, LeadStatus.ACCEPTED, "Lead Accepted successfully")
    }

    fun rejectLead(leadId: String) {
        updateStatus(leadId, LeadStatus.REJECTED, "Lead Rejected")
    }

    fun completeLead(leadId: String) {
        updateStatus(leadId, LeadStatus.COMPLETED, "Lead marked as Completed")
    }

    private fun updateStatus(leadId: String, newStatus: LeadStatus, successMessage: String) {
        viewModelScope.launch {
            val result = updateLeadStatusUseCase(leadId, newStatus)
            result.fold(
                onSuccess = {
                    val updatedList = _uiState.value.allLeads.map { lead ->
                        if (lead.id == leadId) lead.copy(status = newStatus) else lead
                    }
                    _uiState.value = _uiState.value.copy(
                        allLeads = updatedList,
                        filteredLeads = filterLeads(updatedList, _uiState.value.selectedFilter),
                        selectedLeadForDetail = _uiState.value.selectedLeadForDetail?.let {
                            if (it.id == leadId) it.copy(status = newStatus) else it
                        }
                    )
                    _eventFlow.emit(UiEvent.ShowSnackbar(successMessage))
                },
                onFailure = { error ->
                    _eventFlow.emit(UiEvent.ShowSnackbar("Action failed: ${error.localizedMessage}"))
                }
            )
        }
    }

    private fun filterLeads(leads: List<Lead>, filter: LeadFilter): List<Lead> {
        return when (filter) {
            LeadFilter.ALL -> leads
            LeadFilter.NEW -> leads.filter { it.status == LeadStatus.NEW }
            LeadFilter.QUOTED -> leads.filter { it.status == LeadStatus.QUOTED }
            LeadFilter.ACCEPTED -> leads.filter { it.status == LeadStatus.ACCEPTED }
            LeadFilter.REJECTED -> leads.filter { it.status == LeadStatus.REJECTED }
            LeadFilter.COMPLETED -> leads.filter { it.status == LeadStatus.COMPLETED }
        }
    }
}
