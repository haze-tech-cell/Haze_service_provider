package com.haze.servicesprovider.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.presentation.components.BottomNavTab
import com.haze.servicesprovider.presentation.components.HazeBottomNavBar
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.DividerColor
import com.haze.servicesprovider.presentation.theme.ErrorRed
import com.haze.servicesprovider.presentation.theme.ErrorRedLight
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun SettingsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLeads: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    // Logout Confirmation Dialog
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showLogoutDialog(false) },
            title = {
                Text(text = "Log Out", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "Are you sure you want to log out of your provider account?")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.logout(onLoggedOut) }
                ) {
                    Text(text = "Log Out", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showLogoutDialog(false) }) {
                    Text(text = "Cancel", color = SecondaryText)
                }
            },
            containerColor = PureWhite
        )
    }

    // Delete Account Confirmation Dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteDialog(false) },
            title = {
                Text(text = "Delete Account?", fontWeight = FontWeight.Bold, color = ErrorRed)
            },
            text = {
                Text(text = "This will permanently delete your provider profile and remove your listings from Haze Services. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccount(onLoggedOut) }
                ) {
                    Text(text = "Delete Permanently", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteDialog(false) }) {
                    Text(text = "Cancel", color = SecondaryText)
                }
            },
            containerColor = PureWhite
        )
    }

    Scaffold(
        topBar = {
            HazeTopAppBar(title = "Settings")
        },
        bottomBar = {
            HazeBottomNavBar(
                currentRoute = BottomNavTab.Settings.route,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.Home -> onNavigateToHome()
                        BottomNavTab.Leads -> onNavigateToLeads()
                        BottomNavTab.Settings -> Unit
                    }
                }
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
                .padding(16.dp)
        ) {
            // Profile Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToProfile),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!profile?.profilePhotoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = profile.profilePhotoUrl,
                            contentDescription = "Profile Photo",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, ProfessionalBlue, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ProfessionalBlueLight)
                                .border(2.dp, ProfessionalBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.firstName?.take(1)?.uppercase() ?: "P",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = ProfessionalBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile?.fullName ?: "Provider Partner",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkNavyText
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profile?.displayCategory ?: "Service Category",
                            style = MaterialTheme.typography.bodySmall.copy(color = ProfessionalBlue, fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "+91 ${profile?.phoneNumber.orEmpty()}",
                            style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Profile",
                        tint = SecondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Account Group
            SettingsGroup(title = "Account") {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "My Profile",
                    subtitle = "View full business details & registration",
                    onClick = onNavigateToProfile
                )
                HorizontalDivider(color = DividerColor)
                SettingsItem(
                    icon = Icons.Default.Edit,
                    title = "Edit Business Details",
                    subtitle = "Update name, address, or service category",
                    onClick = onNavigateToEditProfile
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preferences Group
            SettingsGroup(title = "Preferences") {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Notification Settings",
                    subtitle = "Manage new enquiries and SMS alerts",
                    onClick = onNavigateToNotifications
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Support & Legal Group
            SettingsGroup(title = "Support & Legal") {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = "Help & Support",
                    subtitle = "FAQs, customer care, and partner assistance",
                    onClick = onNavigateToHelp
                )
                HorizontalDivider(color = DividerColor)
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About Haze Services",
                    subtitle = "App version, mission, and company info",
                    onClick = onNavigateToAbout
                )
                HorizontalDivider(color = DividerColor)
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "How we protect and use your data",
                    onClick = onNavigateToPrivacy
                )
                HorizontalDivider(color = DividerColor)
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Terms & Conditions",
                    subtitle = "Service provider guidelines and policies",
                    onClick = onNavigateToTerms
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions Group
            SettingsGroup(title = "Account Actions") {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Log Out",
                    subtitle = "Sign out from this device",
                    iconTint = DarkNavyText,
                    onClick = { viewModel.showLogoutDialog(true) }
                )
                HorizontalDivider(color = DividerColor)
                SettingsItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Delete Account",
                    subtitle = "Permanently remove your account and data",
                    iconTint = ErrorRed,
                    titleColor = ErrorRed,
                    onClick = { viewModel.showDeleteDialog(true) }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = SecondaryText,
                fontSize = 13.sp
            ),
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = ProfessionalBlue,
    titleColor: Color = DarkNavyText
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (iconTint == ErrorRed) ErrorRedLight else ProfessionalBlueLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(18.dp)
        )
    }
}
