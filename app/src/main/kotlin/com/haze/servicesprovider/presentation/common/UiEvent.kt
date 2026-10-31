package com.haze.servicesprovider.presentation.common

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class ShowToast(val message: String) : UiEvent
    data class Navigate(val route: String, val popUpToRoute: String? = null, val inclusive: Boolean = false) : UiEvent
    data object NavigateUp : UiEvent
}
