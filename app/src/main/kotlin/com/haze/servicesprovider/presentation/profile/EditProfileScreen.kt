package com.haze.servicesprovider.presentation.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.presentation.components.HazePrimaryButton
import com.haze.servicesprovider.presentation.components.HazeTextField
import com.haze.servicesprovider.presentation.components.HazeTopAppBar
import com.haze.servicesprovider.presentation.components.getCategoryIcon
import com.haze.servicesprovider.presentation.registration.CategorySearchBottomSheet
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
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel
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
            selectedCategoryName = uiState.editCategory,
            onCategorySelected = { viewModel.onCategorySelect(it) },
            onDismissRequest = { viewModel.setCategorySheetVisible(false) }
        )
    }

    Scaffold(
        topBar = {
            HazeTopAppBar(
                title = "Edit Business Profile",
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
                .padding(20.dp)
        ) {
            HazeTextField(
                value = uiState.editFullName,
                onValueChange = { viewModel.onFullNameChange(it) },
                label = "Full Name *",
                placeholder = "Full Name",
                errorMessage = uiState.editFullNameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            HazeTextField(
                value = uiState.editSecondaryPhone,
                onValueChange = { viewModel.onSecondaryPhoneChange(it) },
                label = "Secondary Mobile Number",
                placeholder = "Alternative phone number",
                errorMessage = uiState.editSecondaryPhoneError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            HazeTextField(
                value = uiState.editCity,
                onValueChange = { viewModel.onCityChange(it) },
                label = "City *",
                placeholder = "e.g. Mumbai, Bengaluru",
                errorMessage = uiState.editCityError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            HazeTextField(
                value = uiState.editAddress,
                onValueChange = { viewModel.onAddressChange(it) },
                label = "Full Address / Workshop *",
                placeholder = "Address details",
                errorMessage = uiState.editAddressError,
                singleLine = false,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Service Category selector
            Text(
                text = "Service Category *",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkNavyText
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PureWhite)
                    .border(
                        width = 1.dp,
                        color = if (uiState.editCategoryError != null) ErrorRed else CardBorder,
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ProfessionalBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = getCategoryIcon(uiState.editCategory),
                                contentDescription = null,
                                tint = ProfessionalBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.editCategory.ifBlank { "Select Category" },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DarkNavyText
                            )
                        )
                    }

                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select",
                        tint = SecondaryText
                    )
                }
            }

            if (uiState.editCategory.equals("Other", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(14.dp))
                HazeTextField(
                    value = uiState.editCustomCategory,
                    onValueChange = { viewModel.onCustomCategoryChange(it) },
                    label = "Enter Your Service Category *",
                    placeholder = "e.g. Glass Work, Solar Installation",
                    errorMessage = uiState.editCategoryError
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            HazePrimaryButton(
                text = "Save Changes",
                isLoading = uiState.isSaving,
                onClick = {
                    viewModel.saveProfileChanges(onSuccess = onNavigateBack)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
