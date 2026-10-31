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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.domain.model.Lead
import com.haze.servicesprovider.domain.model.LeadStatus
import com.haze.servicesprovider.presentation.components.StatusBadge
import com.haze.servicesprovider.presentation.theme.CardBorder
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
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

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
        ) {
            // Header with status
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

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            // Details Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ProfessionalBlueLight.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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

            Spacer(modifier = Modifier.height(18.dp))

            // Description / Note
            Text(
                text = "Service Details & Requirements",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = lead.description.ifBlank { "No additional requirements specified by customer." },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Actions depending on current status
            when (lead.status) {
                LeadStatus.NEW -> {
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
                            colors = ButtonDefaults.buttonColors(containerColor = ProfessionalBlue)
                        ) {
                            Text(text = "Accept Lead", fontWeight = FontWeight.SemiBold)
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
                LeadStatus.COMPLETED, LeadStatus.REJECTED -> {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ProfessionalBlue)
                    ) {
                        Text(text = "Done", fontWeight = FontWeight.SemiBold)
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
