package com.haze.servicesprovider.presentation.home

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haze.servicesprovider.presentation.components.BottomNavTab
import com.haze.servicesprovider.presentation.components.CategoryCard
import com.haze.servicesprovider.presentation.components.HazeBottomNavBar
import com.haze.servicesprovider.presentation.components.StatusBadge
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.MutedText
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header Bar
            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onNavigateToProfile)
                    ) {
                        // Profile Avatar
                        if (!profile?.profilePhotoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = profile.profilePhotoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, ProfessionalBlue, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(ProfessionalBlueLight)
                                    .border(1.5.dp, ProfessionalBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile?.firstName?.take(1)?.uppercase() ?: "P",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = ProfessionalBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                            )
                            Text(
                                text = profile?.firstName ?: "Partner",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkNavyText
                                )
                            )
                        }
                    }

                    // Notification Icon with Future Badge Support
                    IconButton(onClick = onNotificationClick) {
                        BadgedBox(
                            badge = {
                                if (uiState.unreadNotificationsCount > 0) {
                                    Badge(containerColor = ProfessionalBlue) {
                                        Text(text = uiState.unreadNotificationsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = DarkNavyText
                            )
                        }
                    }
                }
            }

            // 2. Search Section
            item(span = { GridItemSpan(3) }) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search service categories...",
                            color = MutedText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SecondaryText
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = ProfessionalBlue,
                        unfocusedBorderColor = CardBorder
                    )
                )
            }

            // 3. Business Profile Summary Card
            item(span = { GridItemSpan(3) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile?.fullName ?: "Business Profile",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkNavyText
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = ProfessionalBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = profile?.displayCategory ?: "Service Provider",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = ProfessionalBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = SecondaryText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = profile?.city ?: "Location Not Set",
                                        style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                                    )
                                }
                            }

                            // Verified / Active Status Pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuccessGreenLight,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Quick Actions
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkNavyText
                    ),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "View Profile",
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
                        title = "View Leads",
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = onNavigateToLeads,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 5. Popular Categories
            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Categories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyText
                        )
                    )
                    Text(
                        text = "${uiState.filteredCategories.size} Available",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                    )
                }
            }

            // Categories Grid items
            items(uiState.filteredCategories.take(9), key = { it.id }) { category ->
                val isSelected = category.name.equals(profile?.serviceCategory, ignoreCase = true)
                CategoryCard(
                    category = category,
                    isSelected = isSelected,
                    onClick = { /* Informative selection */ }
                )
            }

            // 6. Recent Activity
            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkNavyText
                    ),
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }

            item(span = { GridItemSpan(3) }) {
                if (uiState.recentLeads.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No enquiries yet",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkNavyText
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your customer enquiries will appear here as soon as customers book your services.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SecondaryText,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.recentLeads.forEach { lead ->
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
                                            text = lead.serviceCategory,
                                            style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                                        )
                                    }
                                    StatusBadge(status = lead.status)
                                }
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(3) }) {
                Spacer(modifier = Modifier.height(16.dp))
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
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ProfessionalBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ProfessionalBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavyText,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
