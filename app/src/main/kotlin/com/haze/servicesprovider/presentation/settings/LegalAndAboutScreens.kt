package com.haze.servicesprovider.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haze.servicesprovider.BuildConfig
import com.haze.servicesprovider.R
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "About Haze Services",
                onBackClick = onNavigateBack
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_haze_logo),
                contentDescription = "Haze Services",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Haze Services Provider",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )

            Text(
                text = "Version 1.0.0 (Build 1)",
                style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Empowering Skilled Local Professionals",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyText
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Haze Services connects skilled local technicians, plumbers, electricians, mechanics, and service experts directly with households and commercial clients in their cities.\n\nOur mission is to help local service professionals scale their businesses, increase their earnings, and build trusted, verified reputations in their communities.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SecondaryText,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Technology & Security",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyText
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Built with modern Android Jetpack Compose, Kotlin Coroutines, and Supabase cloud infrastructure with enterprise-grade Row-Level Security (RLS) and encrypted session storage.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = SecondaryText,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Privacy Policy",
                onBackClick = onNavigateBack
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Haze Services Partner Privacy Policy",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyText
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Last updated: October 2026", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                    Spacer(modifier = Modifier.height(14.dp))

                    PolicySection(
                        heading = "1. Information We Collect",
                        content = "We collect your business name, mobile number, service category, address, and profile photograph to create your provider listing and facilitate customer bookings."
                    )

                    PolicySection(
                        heading = "2. How Your Data is Used",
                        content = "Your contact information and service categories are made available to customers who request bookings for your listed services in your designated city."
                    )

                    PolicySection(
                        heading = "3. Security and Storage",
                        content = "All profile data is protected using Row Level Security (RLS) policies in Supabase. Your credentials and sessions are secured using encrypted local storage."
                    )

                    PolicySection(
                        heading = "4. Account Deletion and Rights",
                        content = "You have the right to request deletion of your account and personal data at any time via Settings > Delete Account."
                    )
                }
            }
        }
    }
}

@Composable
fun TermsConditionsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Terms & Conditions",
                onBackClick = onNavigateBack
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Service Provider Terms of Service",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyText
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Effective Date: October 2026", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText))
                    Spacer(modifier = Modifier.height(14.dp))

                    PolicySection(
                        heading = "1. Eligibility & Registration",
                        content = "You agree that all information provided during registration (including your name, mobile number, and service skills) is authentic and accurate."
                    )

                    PolicySection(
                        heading = "2. Professional Standards",
                        content = "Providers are expected to maintain professional conduct, punctuality, and fair service pricing when fulfilling customer requests."
                    )

                    PolicySection(
                        heading = "3. Lead Acceptance",
                        content = "Accepting an enquiry forms an agreement to contact the customer promptly to confirm the appointment and service scope."
                    )

                    PolicySection(
                        heading = "4. Termination",
                        content = "Haze Services reserves the right to suspend or terminate accounts that violate community safety rules or indulge in fraudulent activity."
                    )
                }
            }
        }
    }
}

@Composable
private fun PolicySection(
    heading: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            text = heading,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = DarkNavyText
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = SecondaryText,
                lineHeight = 20.sp
            )
        )
    }
}
