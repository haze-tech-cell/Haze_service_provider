package com.haze.servicesprovider.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.domain.model.ServiceCategory
import com.haze.servicesprovider.domain.model.UserProfile
import com.haze.servicesprovider.domain.usecase.GetCategoriesUseCase
import com.haze.servicesprovider.domain.usecase.GetProfileUseCase
import com.haze.servicesprovider.domain.usecase.SaveProfileUseCase
import com.haze.servicesprovider.domain.usecase.UploadProfilePhotoUseCase
import com.haze.servicesprovider.presentation.common.Resource
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: UserProfile? = null,
    val editFullName: String = "",
    val editFullNameError: String? = null,
    val editSecondaryPhone: String = "",
    val editSecondaryPhoneError: String? = null,
    val editCity: String = "",
    val editCityError: String? = null,
    val editAddress: String = "",
    val editAddressError: String? = null,
    val editCategory: String = "",
    val editCustomCategory: String = "",
    val editCategoryError: String? = null,
    val availableCategories: List<ServiceCategory> = emptyList(),
    val isCategorySheetOpen: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val saveResource: Resource<UserProfile> = Resource.Idle
)

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase = ServiceLocator.getProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase = ServiceLocator.saveProfileUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase = ServiceLocator.uploadProfilePhotoUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase = ServiceLocator.getCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        observeProfile()
        loadCategories()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            getProfileUseCase.observe().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    profile = profile,
                    editFullName = profile?.fullName.orEmpty(),
                    editSecondaryPhone = profile?.secondaryPhoneNumber.orEmpty(),
                    editCity = profile?.city.orEmpty(),
                    editAddress = profile?.fullAddress.orEmpty(),
                    editCategory = profile?.serviceCategory.orEmpty(),
                    editCustomCategory = profile?.customServiceCategory.orEmpty()
                )
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val defaults = getCategoriesUseCase.getDefaults()
            _uiState.value = _uiState.value.copy(availableCategories = defaults)
            val remoteResult = getCategoriesUseCase()
            remoteResult.getOrNull()?.let { cats ->
                if (cats.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(availableCategories = cats)
                }
            }
        }
    }

    fun onFullNameChange(name: String) {
        _uiState.value = _uiState.value.copy(editFullName = name, editFullNameError = null)
    }

    fun onSecondaryPhoneChange(phone: String) {
        val filtered = phone.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(editSecondaryPhone = filtered, editSecondaryPhoneError = null)
    }

    fun onCityChange(city: String) {
        _uiState.value = _uiState.value.copy(editCity = city, editCityError = null)
    }

    fun onAddressChange(address: String) {
        _uiState.value = _uiState.value.copy(editAddress = address, editAddressError = null)
    }

    fun onCategorySelect(category: ServiceCategory) {
        _uiState.value = _uiState.value.copy(
            editCategory = category.name,
            editCategoryError = null,
            isCategorySheetOpen = false
        )
    }

    fun onCustomCategoryChange(custom: String) {
        _uiState.value = _uiState.value.copy(editCustomCategory = custom, editCategoryError = null)
    }

    fun setCategorySheetVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isCategorySheetOpen = visible)
    }

    fun uploadPhoto(imageBytes: ByteArray, extension: String = "jpg") {
        val currentProfile = _uiState.value.profile ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingPhoto = true)
            val result = uploadProfilePhotoUseCase(currentProfile.id, imageBytes, extension)
            result.fold(
                onSuccess = { url ->
                    val updated = currentProfile.copy(profilePhotoUrl = url)
                    _uiState.value = _uiState.value.copy(profile = updated, isUploadingPhoto = false)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Profile photo updated successfully"))
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isUploadingPhoto = false)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Photo upload failed: ${error.localizedMessage}"))
                }
            )
        }
    }

    fun saveProfileChanges(onSuccess: () -> Unit) {
        val currentProfile = _uiState.value.profile ?: return
        val state = _uiState.value

        val nameVal = ValidationUtils.validateFullName(state.editFullName)
        val secPhoneVal = ValidationUtils.validateSecondaryMobileNumber(
            state.editSecondaryPhone,
            currentProfile.phoneNumber
        )
        val cityVal = ValidationUtils.validateCity(state.editCity)
        val addressVal = ValidationUtils.validateAddress(state.editAddress)
        val catVal = ValidationUtils.validateCategory(state.editCategory, state.editCustomCategory)

        if (!nameVal.isValid || !secPhoneVal.isValid || !cityVal.isValid || !addressVal.isValid || !catVal.isValid) {
            _uiState.value = state.copy(
                editFullNameError = nameVal.errorMessage,
                editSecondaryPhoneError = secPhoneVal.errorMessage,
                editCityError = cityVal.errorMessage,
                editAddressError = addressVal.errorMessage,
                editCategoryError = catVal.errorMessage
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val updated = currentProfile.copy(
                fullName = nameVal.cleanedValue ?: state.editFullName.trim(),
                secondaryPhoneNumber = if (state.editSecondaryPhone.isNotBlank()) state.editSecondaryPhone.trim() else null,
                city = cityVal.cleanedValue ?: state.editCity.trim(),
                fullAddress = addressVal.cleanedValue ?: state.editAddress.trim(),
                serviceCategory = state.editCategory.trim(),
                customServiceCategory = if (state.editCategory.equals("Other", ignoreCase = true)) {
                    state.editCustomCategory.trim()
                } else null
            )

            val result = saveProfileUseCase(updated, isNew = false)
            result.fold(
                onSuccess = { saved ->
                    _uiState.value = _uiState.value.copy(profile = saved, isSaving = false)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Profile updated successfully"))
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Update failed: ${error.localizedMessage}"))
                }
            )
        }
    }
}
