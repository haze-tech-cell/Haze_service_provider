package com.haze.servicesprovider.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.DividerColor
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Notification Settings",
                onBackClick = onNavigateBack
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(
                text = "Configure how and when you receive alerts about customer enquiries and business updates.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    NotificationToggleRow(
                        title = "New Customer Enquiries",
                        subtitle = "Instant push notification when a customer requests your service",
                        checked = uiState.newLeadsNotification,
                        onCheckedChange = { viewModel.toggleNewLeads(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    NotificationToggleRow(
                        title = "SMS Alerts for Urgent Leads",
                        subtitle = "Receive an SMS text when an enquiry requires quick confirmation",
                        checked = uiState.smsAlerts,
                        onCheckedChange = { viewModel.toggleSms(it) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)

                    NotificationToggleRow(
                        title = "Business Tips & Growth Offers",
                        subtitle = "Seasonal promotions and service growth advice",
                        checked = uiState.promotionalNotification,
                        onCheckedChange = { viewModel.togglePromotions(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavyText
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SecondaryText,
                    lineHeight = 18.sp
                )
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PureWhite,
                checkedTrackColor = ProfessionalBlue
            )
        )
    }
}
