package com.haze.servicesprovider.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.domain.model.ServiceCategory
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "plumbing", "plumber" -> Icons.Default.Plumbing
        "electricbolt", "electrician" -> Icons.Default.ElectricBolt
        "formatpaint", "painter" -> Icons.Default.FormatPaint
        "carpenter" -> Icons.Default.Carpenter
        "acunit", "ac_technician" -> Icons.Default.AcUnit
        "tv", "tv_repair" -> Icons.Default.Tv
        "kitchen", "refrigerator_repair" -> Icons.Default.Kitchen
        "locallaundryservice", "washing_machine_repair" -> Icons.Default.LocalLaundryService
        "waterdrop", "ro_repair" -> Icons.Default.WaterDrop
        "smartphone", "mobile_repair" -> Icons.Default.Smartphone
        "computer", "computer_repair" -> Icons.Default.Computer
        "build", "mechanic" -> Icons.Default.Build
        "cleaningservices", "cleaning_services" -> Icons.Default.CleaningServices
        "pestcontrol", "pest_control" -> Icons.Default.PestControl
        "contentcut", "salon_services" -> Icons.Default.ContentCut
        "homerepairservice", "appliance_repair" -> Icons.Default.HomeRepairService
        else -> Icons.Default.Category
    }
}

@Composable
fun CategoryCard(
    category: ServiceCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) ProfessionalBlue else CardBorder
    val bgColor = if (isSelected) ProfessionalBlueLight.copy(alpha = 0.5f) else PureWhite

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (isSelected) ProfessionalBlue else ProfessionalBlueLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(category.iconName),
                contentDescription = category.name,
                tint = if (isSelected) PureWhite else ProfessionalBlue,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (isSelected) ProfessionalBlue else DarkNavyText,
                textAlign = TextAlign.Center
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
