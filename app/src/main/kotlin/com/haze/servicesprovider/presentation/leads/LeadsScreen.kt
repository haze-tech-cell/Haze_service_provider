package com.haze.servicesprovider.presentation.leads

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.presentation.components.BottomNavTab
import com.haze.servicesprovider.presentation.components.EmptyStateView
import com.haze.servicesprovider.presentation.components.HazeBottomNavBar
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.components.StatusBadge
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText
import com.haze.servicesprovider.utils.DateUtils

@Composable
fun LeadsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: LeadsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    if (uiState.selectedLeadForDetail != null) {
        LeadDetailBottomSheet(
            lead = uiState.selectedLeadForDetail!!,
            onAccept = { viewModel.acceptLead(uiState.selectedLeadForDetail!!.id) },
            onReject = { viewModel.rejectLead(uiState.selectedLeadForDetail!!.id) },
            onComplete = { viewModel.completeLead(uiState.selectedLeadForDetail!!.id) },
            onSendQuotation = { charges, arrival, notes ->
                viewModel.sendQuotation(uiState.selectedLeadForDetail!!.id, charges, arrival, notes)
            },
            onDismissRequest = { viewModel.onLeadSelected(null) }
        )
    }

    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Customer Enquiries",
                actions = {
                    IconButton(onClick = { viewModel.loadLeads() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = DarkNavyText
                        )
                    }
                }
            )
        },
        bottomBar = {
            HazeBottomNavBar(
                currentRoute = BottomNavTab.Leads.route,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.Home -> onNavigateToHome()
                        BottomNavTab.Leads -> Unit
                        BottomNavTab.Settings -> onNavigateToSettings()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Pills Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LeadFilter.entries) { filter ->
                    val isSelected = uiState.selectedFilter == filter

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onFilterSelected(filter) },
                        label = {
                            Text(
                                text = filter.displayName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProfessionalBlue,
                            selectedLabelColor = PureWhite,
                            containerColor = PureWhite,
                            labelColor = SecondaryText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ProfessionalBlue else CardBorder
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // Leads List / Empty State / Loading
            if (uiState.isLoading && uiState.allLeads.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ProfessionalBlue, strokeWidth = 3.dp)
                }
            } else if (uiState.filteredLeads.isEmpty()) {
                EmptyStateView(
                    title = "No enquiries yet",
                    subtitle = "Your customer enquiries will appear here.",
                    icon = Icons.Outlined.Inbox
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredLeads, key = { it.id }) { lead ->
                        LeadCard(
                            lead = lead,
                            onClick = { viewModel.onLeadSelected(lead) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeadCard(
    lead: Lead,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = lead.customerName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyText
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    StatusBadge(status = lead.status)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Service: ${lead.serviceCategory}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ProfessionalBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = lead.description.ifBlank { "Service enquiry from customer" },
                    style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = DateUtils.formatIsoToReadable(lead.createdAt),
                    style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Details",
                tint = SecondaryText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
