package com.haze.servicesprovider.presentation.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haze.servicesprovider.presentation.components.BottomNavTab
import com.haze.servicesprovider.presentation.components.HazeBottomNavBar
import com.haze.servicesprovider.presentation.components.StatusBadge
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText
import com.haze.servicesprovider.presentation.theme.SuccessGreen
import com.haze.servicesprovider.presentation.theme.SuccessGreenLight

@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToLeads: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNotificationClick: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val context = LocalContext.current

    // On home screen, pressing back minimizes app instead of crashing or popping out
    BackHandler {
        (context as? Activity)?.moveTaskToBack(true)
    }

    Scaffold(
        bottomBar = {
            HazeBottomNavBar(
                currentRoute = BottomNavTab.Home.route,
                onTabSelected = { tab ->
                    when (tab) {
                        BottomNavTab.Home -> Unit
                        BottomNavTab.Leads -> onNavigateToLeads()
                        BottomNavTab.Settings -> onNavigateToSettings()
                    }
                }
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Header: User Name on Top-Left
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onNavigateToProfile)
                    ) {
                        // Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ProfessionalBlueLight)
                                .border(1.5.dp, ProfessionalBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.fullName?.take(1)?.uppercase() ?: "P",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = ProfessionalBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // User Name on Top-Left
                        Column {
                            Text(
                                text = "Welcome,",
                                style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                            )
                            Text(
                                text = profile?.fullName?.ifBlank { "Service Partner" } ?: "Service Partner",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkNavyText,
                                    fontSize = 19.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = SecondaryText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${profile?.displayCategory ?: "Service"} • ${profile?.city ?: "Bhopal"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = SecondaryText,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }

                    // Active Status Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SuccessGreenLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // 2. Ad Box (Empty Advertisement Banner Space)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFF8FAFC),
                                        Color(0xFFEEF2FF)
                                    )
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Ad Space",
                                tint = ProfessionalBlue,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Advertisement Space",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DarkNavyText
                            )
                            Text(
                                text = "Offers, updates & sponsor ads will appear here",
                                fontSize = 11.5.sp,
                                color = SecondaryText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 3. Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkNavyText
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "My Profile",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToProfile,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Edit Details",
                        icon = Icons.Default.Edit,
                        onClick = onNavigateToEditProfile,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Customer Leads",
                        icon = Icons.AutoMirrored.Filled.Assignment,
                        onClick = onNavigateToLeads,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Recent Customer Requests Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Customer Enquiries",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyText
                        )
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ProfessionalBlue,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable(onClick = onNavigateToLeads)
                            .padding(4.dp)
                    )
                }
            }

            // 5. Recent Leads List / Empty State
            if (uiState.recentLeads.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, CardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Inbox,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Waiting for customer bookings",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkNavyText
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Whenever a customer in ${profile?.city ?: "your city"} requests ${profile?.displayCategory ?: "your service"}, it will show up here.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SecondaryText,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            } else {
                items(uiState.recentLeads.take(4)) { lead ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToLeads),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lead.customerName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkNavyText
                                    )
                                )
                                Text(
                                    text = "${lead.serviceCategory} • ${lead.city}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                                )
                            }
                            StatusBadge(status = lead.status)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = PureWhite
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ProfessionalBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ProfessionalBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavyText,
                    fontSize = 11.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
