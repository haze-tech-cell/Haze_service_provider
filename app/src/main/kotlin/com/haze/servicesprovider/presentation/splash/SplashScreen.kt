package com.haze.servicesprovider.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.haze.servicesprovider.R
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMobileRegistration: () -> Unit,
    onNavigateToCompleteRegistration: (String) -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (val dest = destination) {
            is SplashDestination.Home -> onNavigateToHome()
            is SplashDestination.MobileRegistration -> onNavigateToMobileRegistration()
            is SplashDestination.CompleteRegistration -> onNavigateToCompleteRegistration(dest.phoneNumber)
            null -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_haze_logo),
                contentDescription = "Haze Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.app_tagline),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = ProfessionalBlue,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
