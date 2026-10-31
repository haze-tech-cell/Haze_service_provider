package com.haze.servicesprovider.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.domain.model.LeadStatus
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ErrorRed
import com.haze.servicesprovider.presentation.theme.ErrorRedLight
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.SuccessGreen
import com.haze.servicesprovider.presentation.theme.SuccessGreenLight
import com.haze.servicesprovider.presentation.theme.WarningAmber
import com.haze.servicesprovider.presentation.theme.WarningAmberLight

@Composable
fun StatusBadge(
    status: LeadStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        LeadStatus.NEW -> ProfessionalBlueLight to ProfessionalBlue
        LeadStatus.QUOTED -> Color(0xFFEDE9FE) to Color(0xFF6D28D9) // Purple
        LeadStatus.ACCEPTED -> SuccessGreenLight to SuccessGreen
        LeadStatus.REJECTED -> ErrorRedLight to ErrorRed
        LeadStatus.COMPLETED -> WarningAmberLight to WarningAmber
        LeadStatus.CANCELLED -> Color(0xFFF1F5F9) to Color(0xFF64748B)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}
