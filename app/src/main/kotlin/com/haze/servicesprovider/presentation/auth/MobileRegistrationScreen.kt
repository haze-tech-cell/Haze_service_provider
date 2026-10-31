package com.haze.servicesprovider.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haze.servicesprovider.R
import com.haze.servicesprovider.presentation.common.Resource
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.presentation.components.HazePrimaryButton
import com.haze.servicesprovider.presentation.components.HazeTextField
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun MobileRegistrationScreen(
    onNavigateToOtp: (String) -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
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

    // Demo OTP Popup
    if (uiState.showDemoOtpPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDemoOtpPopup() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDE9FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "OTP Key",
                        tint = Color(0xFF6D28D9),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Verification Code (Demo) 🔑",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = DarkNavyText,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Mobile: +91 ${uiState.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "YOUR OTP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SecondaryText,
                                    letterSpacing = 2.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = uiState.demoOtpCode,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF6D28D9),
                                    letterSpacing = 6.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "SMS Gateway testing mode me hai. Testing ke liye ye code enter karein.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecondaryText,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmDemoOtpAndProceed(onNavigateToOtp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ProfessionalBlue)
                ) {
                    Text("Auto-Fill & Verify", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissDemoOtpPopup() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }

    Scaffold(
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
            Spacer(modifier = Modifier.height(28.dp))

            // App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_haze_logo),
                contentDescription = "Haze Services",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Heading & Subtitle
            Text(
                text = "Partner Registration",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText,
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your mobile number to register your service business and receive customer enquiries",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Mobile Number Input with fixed +91 prefix
            HazeTextField(
                value = uiState.phoneNumber,
                onValueChange = { viewModel.onPhoneNumberChange(it) },
                label = "Mobile Number",
                placeholder = "98765 43210",
                errorMessage = uiState.phoneErrorMessage,
                prefix = {
                    Surface(
                        color = PureWhite,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🇮🇳 +91",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkNavyText
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(20.dp)
                                    .background(CardBorder)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (uiState.isPhoneValid) {
                            viewModel.sendOtp(onNavigateToOtp)
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Continue Button
            HazePrimaryButton(
                text = "Continue with OTP",
                enabled = uiState.isPhoneValid,
                isLoading = uiState.sendOtpResource is Resource.Loading,
                onClick = {
                    focusManager.clearFocus()
                    viewModel.sendOtp(onNavigateToOtp)
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Terms & Privacy Policy Footnote
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "By continuing, you agree to Haze Provider",
                    style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = "Terms & Conditions",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ProfessionalBlue,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable(onClick = onNavigateToTerms)
                    )

                    Text(
                        text = " and ",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                    )

                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ProfessionalBlue,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable(onClick = onNavigateToPrivacy)
                    )
                }
            }
        }
    }
}
