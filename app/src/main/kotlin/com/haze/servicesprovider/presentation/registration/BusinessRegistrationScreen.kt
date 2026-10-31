package com.haze.servicesprovider.presentation.registration

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haze.servicesprovider.presentation.common.Resource
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.presentation.components.HazePrimaryButton
import com.haze.servicesprovider.presentation.components.HazeTextField
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.components.getCategoryIcon
import com.haze.servicesprovider.presentation.theme.BackgroundLight
import com.haze.servicesprovider.presentation.theme.CardBorder
import com.haze.servicesprovider.presentation.theme.DarkNavyText
import com.haze.servicesprovider.presentation.theme.ErrorRed
import com.haze.servicesprovider.presentation.theme.MutedText
import com.haze.servicesprovider.presentation.theme.ProfessionalBlue
import com.haze.servicesprovider.presentation.theme.ProfessionalBlueLight
import com.haze.servicesprovider.presentation.theme.PureWhite
import com.haze.servicesprovider.presentation.theme.SecondaryText

@Composable
fun BusinessRegistrationScreen(
    phoneNumber: String,
    onRegistrationSuccess: () -> Unit,
    viewModel: BusinessRegistrationViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    if (uiState.isCategorySheetOpen) {
        CategorySearchBottomSheet(
            categories = uiState.availableCategories,
            selectedCategoryName = uiState.serviceCategory,
            onCategorySelected = { viewModel.onCategorySelect(it) },
            onDismissRequest = { viewModel.setCategorySheetVisible(false) }
        )
    }

    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Business Profile Setup"
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Description
            Text(
                text = "Complete your business details to start getting service leads from local customers.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = SecondaryText,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION A: Personal Information
            SectionCard(
                title = "Personal Information",
                icon = Icons.Default.Person
            ) {
                HazeTextField(
                    value = uiState.fullName,
                    onValueChange = { viewModel.onFullNameChange(it) },
                    label = "Full Name *",
                    placeholder = "e.g. Ramesh Kumar",
                    errorMessage = uiState.fullNameError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                HazeTextField(
                    value = uiState.secondaryPhone,
                    onValueChange = { viewModel.onSecondaryPhoneChange(it) },
                    label = "Secondary Mobile Number (Optional)",
                    placeholder = "Alternative contact number",
                    errorMessage = uiState.secondaryPhoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION B: Location Information
            SectionCard(
                title = "Location Information",
                icon = Icons.Default.LocationOn
            ) {
                HazeTextField(
                    value = uiState.city,
                    onValueChange = { viewModel.onCityChange(it) },
                    label = "City *",
                    placeholder = "e.g. Mumbai, Delhi, Bengaluru",
                    errorMessage = uiState.cityError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                HazeTextField(
                    value = uiState.fullAddress,
                    onValueChange = { viewModel.onAddressChange(it) },
                    label = "Full Address / Workshop Location *",
                    placeholder = "Shop/Office No., Street, Area, Landmark, Pincode",
                    errorMessage = uiState.fullAddressError,
                    singleLine = false,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION C: Business Information
            SectionCard(
                title = "Business Information",
                icon = Icons.Default.Business
            ) {
                Text(
                    text = "Primary Service Category *",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkNavyText
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Searchable Category Picker Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PureWhite)
                        .border(
                            width = 1.dp,
                            color = if (uiState.categoryError != null) ErrorRed else CardBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.setCategorySheetVisible(true) }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (uiState.serviceCategory.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(ProfessionalBlueLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(uiState.serviceCategory),
                                        contentDescription = null,
                                        tint = ProfessionalBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = uiState.serviceCategory,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkNavyText
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "Select your service category",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MutedText)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select",
                            tint = SecondaryText
                        )
                    }
                }

                if (uiState.categoryError != null) {
                    Text(
                        text = uiState.categoryError.orEmpty(),
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                // If "Other" is selected, show custom category input
                if (uiState.serviceCategory.equals("Other", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HazeTextField(
                        value = uiState.customServiceCategory,
                        onValueChange = { viewModel.onCustomCategoryChange(it) },
                        label = "Enter Your Service Category *",
                        placeholder = "e.g. Solar Panel Installation, Glass Work",
                        errorMessage = uiState.categoryError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Button
            HazePrimaryButton(
                text = "Complete Business Registration",
                isLoading = uiState.saveProfileResource is Resource.Loading,
                onClick = {
                    viewModel.submitRegistration(phoneNumber, onRegistrationSuccess)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
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
                        contentDescription = null,
                        tint = ProfessionalBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkNavyText
                    )
                )
            }

            content()
        }
    }
}
