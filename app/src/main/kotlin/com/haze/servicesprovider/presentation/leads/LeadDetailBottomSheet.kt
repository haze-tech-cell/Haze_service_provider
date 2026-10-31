package com.haze.servicesprovider.presentation.leads

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.LeadStatus
import com.haze.servicesprovider.presentation.components.StatusBadge
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ErrorRed
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText
import com.haze.servicesprovider.presentation.theme.SuccessGreen
import com.haze.servicesprovider.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailBottomSheet(
    lead: Lead,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onComplete: () -> Unit,
    onSendQuotation: (visitingCharges: Double, estimatedArrival: String, notes: String?) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var visitingChargesInput by remember {
        mutableStateOf(if (lead.visitingCharges > 0) lead.visitingCharges.toInt().toString() else "299")
    }
    var arrivalTimeInput by remember {
        mutableStateOf(lead.estimatedArrival ?: "Within 2 Hours")
    }
    var notesInput by remember {
        mutableStateOf(lead.providerNotes ?: "")
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = PureWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(scrollState)
        ) {
            // Header with status & close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = lead.status)

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Customer Name & Title
            Text(
                text = lead.customerName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Service: ${lead.serviceCategory}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ProfessionalBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Details Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ProfessionalBlueLight.copy(alpha = 0.45f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "City & Address",
                        value = "${lead.city}${if (!lead.customerAddress.isNullOrBlank()) " • " + lead.customerAddress else ""}"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        icon = Icons.Default.Call,
                        label = "Contact Number",
                        value = lead.customerPhone,
                        action = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${lead.customerPhone}")
                            }
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        icon = Icons.Default.DateRange,
                        label = "Requested Date",
                        value = DateUtils.formatIsoToReadable(lead.createdAt)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description / Customer Requirement
            Text(
                text = "Customer Requirements",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lead.description.ifBlank { "Service requested via Haze Customer Directory." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    lineHeight = 20.sp
                )
            )

            // Quotation Form for NEW or QUOTED leads
            if (lead.status == LeadStatus.NEW || lead.status == LeadStatus.QUOTED) {
                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (lead.status == LeadStatus.QUOTED) "Your Sent Quotation" else "Send Service Quotation to Customer",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkNavyText
                            )
                        )
                        Text(
                            text = "Customer will see your estimated charge and arrival time instantly.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visiting Charge Field
                        OutlinedTextField(
                            value = visitingChargesInput,
                            onValueChange = { visitingChargesInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Visiting / Service Charge (₹)") },
                            leadingIcon = {
                                Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = ProfessionalBlue)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Estimated Arrival Time Field
                        OutlinedTextField(
                            value = arrivalTimeInput,
                            onValueChange = { arrivalTimeInput = it },
                            label = { Text("Estimated Arrival (e.g. Today 4 PM / Within 1 Hr)") },
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = ProfessionalBlue)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Optional Note
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Note for Customer (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val charges = visitingChargesInput.toDoubleOrNull() ?: 0.0
                                onSendQuotation(charges, arrivalTimeInput.ifBlank { "Within 2 Hours" }, notesInput)
                                onDismissRequest()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)) // Royal Purple
                        ) {
                            Text(
                                text = if (lead.status == LeadStatus.QUOTED) "Update Quotation" else "Send Quotation (₹$visitingChargesInput)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions depending on current status
            when (lead.status) {
                LeadStatus.NEW, LeadStatus.QUOTED -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onReject()
                                onDismissRequest()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                        ) {
                            Text(text = "Reject", fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onAccept()
                                onDismissRequest()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text(text = "Accept Direct", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                LeadStatus.ACCEPTED -> {
                    Button(
                        onClick = {
                            onComplete()
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text(text = "Mark as Completed", fontWeight = FontWeight.SemiBold)
                    }
                }
                LeadStatus.COMPLETED, LeadStatus.REJECTED, LeadStatus.CANCELLED -> {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ProfessionalBlue)
                    ) {
                        Text(text = "Close", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    action: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PureWhite),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ProfessionalBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavyText
                )
            )
        }

        if (action != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ProfessionalBlue,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                IconButton(onClick = action, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Customer",
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
