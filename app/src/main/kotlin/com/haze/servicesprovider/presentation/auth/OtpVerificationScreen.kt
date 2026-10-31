package com.haze.servicesprovider.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haze.servicesprovider.presentation.common.Resource
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.presentation.components.HazePrimaryButton
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.components.OtpInputField
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ErrorRed
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun OtpVerificationScreen(
    phoneNumber: String,
    onNavigateBack: () -> Unit,
    onVerificationSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Verify Mobile",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter 6-Digit Code",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText,
                    fontSize = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "We have sent a verification code to\n+91 $phoneNumber",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Wrong number? Change",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ProfessionalBlue,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clickable(onClick = onNavigateBack)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Demo OTP helper badge
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onOtpChange(uiState.demoOtpCode) },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEDE9FE)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = Color(0xFF6D28D9),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Demo Mode OTP: ${uiState.demoOtpCode}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D28D9),
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "Tap to Auto-Fill",
                        color = Color(0xFF6D28D9),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 6-digit OTP input boxes
            OtpInputField(
                otpValue = uiState.otp,
                onOtpChange = { viewModel.onOtpChange(it) },
                isError = !uiState.otpErrorMessage.isNullOrBlank(),
                onComplete = {
                    focusManager.clearFocus()
                    viewModel.verifyOtp(phoneNumber, onVerificationSuccess)
                }
            )

            if (!uiState.otpErrorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.otpErrorMessage.orEmpty(),
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Verify Button
            HazePrimaryButton(
                text = "Verify & Proceed",
                enabled = uiState.otp.length == 6,
                isLoading = uiState.verifyOtpResource is Resource.Loading,
                onClick = {
                    focusManager.clearFocus()
                    viewModel.verifyOtp(phoneNumber, onVerificationSuccess)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resend Countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.canResendOtp) {
                    Text(
                        text = "Didn't receive code? ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                    )
                    Text(
                        text = "Resend OTP",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ProfessionalBlue,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { viewModel.resendOtp(phoneNumber) }
                            .padding(4.dp)
                    )
                } else {
                    Text(
                        text = "Resend OTP in ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = SecondaryText)
                    )
                    Text(
                        text = "${uiState.resendCountdown}s",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ProfessionalBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
