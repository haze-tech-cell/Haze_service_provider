package com.haze.servicesprovider.presentation.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.presentation.components.HazePrimaryButton
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText
import com.haze.servicesprovider.utils.DateUtils
import java.io.ByteArrayOutputStream

@Composable
fun MyProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, selectedUri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, selectedUri)
                }
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                viewModel.uploadPhoto(stream.toByteArray(), "jpg")
            } catch (e: Exception) {
                // Handle image read failure
            }
        }
    }

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
                title = "My Business Profile",
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Avatar with Edit Badge
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                if (!profile?.profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = profile.profilePhotoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .border(3.dp, ProfessionalBlue, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(ProfessionalBlueLight)
                            .border(3.dp, ProfessionalBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.firstName?.take(1)?.uppercase() ?: "P",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = ProfessionalBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 38.sp
                            )
                        )
                    }
                }

                if (uiState.isUploadingPhoto) {
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .background(PureWhite.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), color = ProfessionalBlue)
                    }
                }

                // Camera Badge Icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ProfessionalBlue)
                        .border(2.dp, PureWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Name & Verified Title
            Text(
                text = profile?.fullName ?: "Provider Partner",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkNavyText
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = profile?.displayCategory ?: "Service Category",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = ProfessionalBlue,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Primary Mobile",
                        value = "+91 ${profile?.phoneNumber.orEmpty()}"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Secondary Mobile",
                        value = if (!profile?.secondaryPhoneNumber.isNullOrBlank()) "+91 ${profile?.secondaryPhoneNumber}" else "Not Added"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "City",
                        value = profile?.city.orEmpty().ifBlank { "Not Set" }
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Home,
                        label = "Full Address",
                        value = profile?.fullAddress.orEmpty().ifBlank { "Not Set" }
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Business,
                        label = "Service Category",
                        value = profile?.displayCategory.orEmpty().ifBlank { "Not Set" }
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Partner Since",
                        value = DateUtils.formatIsoToDateOnly(profile?.createdAt)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Edit Profile Button
            HazePrimaryButton(
                text = "Edit Business Details",
                onClick = onNavigateToEditProfile
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ProfessionalBlueLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ProfessionalBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = SecondaryText)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavyText
                )
            )
        }
    }
}
