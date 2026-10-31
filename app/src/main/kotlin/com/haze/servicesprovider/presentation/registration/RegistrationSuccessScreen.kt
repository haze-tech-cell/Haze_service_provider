package com.haze.servicesprovider.presentation.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.presentation.components.HazePrimaryButton
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.SecondaryText
import com.haze.servicesprovider.presentation.theme.SuccessGreen
import com.haze.servicesprovider.presentation.theme.SuccessGreenLight

@Composable
fun RegistrationSuccessScreen(
    onNavigateToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(SuccessGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = SuccessGreen,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Welcome to Haze Services!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkNavyText,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your business profile has been successfully registered. Customers in your city can now discover your services and request bookings.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SecondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        HazePrimaryButton(
            text = "Go to Business Dashboard",
            onClick = onNavigateToHome
        )
    }
}
