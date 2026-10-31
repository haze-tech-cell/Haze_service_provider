package com.haze.servicesprovider.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haze.servicesprovider.di.ServiceLocator
import com.haze.servicesprovider.domain.usecase.ResendOtpUseCase
import com.haze.servicesprovider.domain.usecase.SendOtpUseCase
import com.haze.servicesprovider.domain.usecase.VerifyOtpUseCase
import com.haze.servicesprovider.presentation.common.Resource
import com.haze.servicesprovider.presentation.common.UiEvent
import com.haze.servicesprovider.utils.AppConfig
import com.haze.servicesprovider.utils.ValidationUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val phoneNumber: String = "",
    val phoneErrorMessage: String? = null,
    val isPhoneValid: Boolean = false,
    val otp: String = "",
    val otpErrorMessage: String? = null,
    val resendCountdown: Int = AppConfig.RESEND_OTP_TIMEOUT_SECONDS,
    val canResendOtp: Boolean = false,
    val sendOtpResource: Resource<Unit> = Resource.Idle,
    val verifyOtpResource: Resource<String> = Resource.Idle
)

class AuthViewModel(
    private val sendOtpUseCase: SendOtpUseCase = ServiceLocator.sendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase = ServiceLocator.verifyOtpUseCase,
    private val resendOtpUseCase: ResendOtpUseCase = ServiceLocator.resendOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    private var countdownJob: Job? = null

    fun onPhoneNumberChange(rawInput: String) {
        val filtered = rawInput.filter { it.isDigit() }.take(10)
        val validation = ValidationUtils.validateIndianMobileNumber(filtered)
        _uiState.value = _uiState.value.copy(
            phoneNumber = filtered,
            isPhoneValid = validation.isValid,
            phoneErrorMessage = if (filtered.length >= 10 && !validation.isValid) validation.errorMessage else null
        )
    }

    fun onOtpChange(otpInput: String) {
        val filtered = otpInput.filter { it.isDigit() }.take(AppConfig.OTP_LENGTH)
        _uiState.value = _uiState.value.copy(
            otp = filtered,
            otpErrorMessage = null
        )
    }

    fun sendOtp(onSuccess: (String) -> Unit) {
        val phone = _uiState.value.phoneNumber
        val validation = ValidationUtils.validateIndianMobileNumber(phone)
        if (!validation.isValid) {
            _uiState.value = _uiState.value.copy(phoneErrorMessage = validation.errorMessage)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sendOtpResource = Resource.Loading)
            val result = sendOtpUseCase(phone)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(sendOtpResource = Resource.Success(Unit))
                    startCountdownTimer()
                    onSuccess(phone)
                },
                onFailure = { error ->
                    val userFriendlyMsg = mapAuthErrorMessage(error)
                    _uiState.value = _uiState.value.copy(
                        sendOtpResource = Resource.Error(userFriendlyMsg, error)
                    )
                    _eventFlow.emit(UiEvent.ShowSnackbar(userFriendlyMsg))
                }
            )
        }
    }

    fun verifyOtp(phoneNumber: String, onSuccess: () -> Unit) {
        val currentOtp = _uiState.value.otp
        val validation = ValidationUtils.validateOtp(currentOtp)
        if (!validation.isValid) {
            _uiState.value = _uiState.value.copy(otpErrorMessage = validation.errorMessage)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(verifyOtpResource = Resource.Loading)
            val result = verifyOtpUseCase(phoneNumber, currentOtp)
            result.fold(
                onSuccess = { userId ->
                    _uiState.value = _uiState.value.copy(verifyOtpResource = Resource.Success(userId))
                    onSuccess()
                },
                onFailure = { error ->
                    val userFriendlyMsg = mapAuthErrorMessage(error)
                    _uiState.value = _uiState.value.copy(
                        verifyOtpResource = Resource.Error(userFriendlyMsg, error),
                        otpErrorMessage = userFriendlyMsg
                    )
                    _eventFlow.emit(UiEvent.ShowSnackbar(userFriendlyMsg))
                }
            )
        }
    }

    fun resendOtp(phoneNumber: String) {
        if (!_uiState.value.canResendOtp) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(sendOtpResource = Resource.Loading)
            val result = resendOtpUseCase(phoneNumber)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(sendOtpResource = Resource.Success(Unit))
                    startCountdownTimer()
                    _eventFlow.emit(UiEvent.ShowSnackbar("OTP resent successfully to $phoneNumber"))
                },
                onFailure = { error ->
                    val userFriendlyMsg = mapAuthErrorMessage(error)
                    _uiState.value = _uiState.value.copy(sendOtpResource = Resource.Error(userFriendlyMsg, error))
                    _eventFlow.emit(UiEvent.ShowSnackbar(userFriendlyMsg))
                }
            )
        }
    }

    private fun startCountdownTimer() {
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(
            resendCountdown = AppConfig.RESEND_OTP_TIMEOUT_SECONDS,
            canResendOtp = false
        )

        countdownJob = viewModelScope.launch {
            for (sec in AppConfig.RESEND_OTP_TIMEOUT_SECONDS downTo 1) {
                _uiState.value = _uiState.value.copy(resendCountdown = sec)
                delay(1000)
            }
            _uiState.value = _uiState.value.copy(resendCountdown = 0, canResendOtp = true)
        }
    }

    private fun mapAuthErrorMessage(error: Throwable): String {
        val msg = error.message.orEmpty().lowercase()
        return when {
            msg.contains("rate limit") -> "Too many attempts. Please wait a moment before trying again."
            msg.contains("invalid token") || msg.contains("invalid otp") || msg.contains("otp expired") -> "Invalid or expired OTP. Please check or request a new code."
            msg.contains("network") || msg.contains("unable to resolve host") -> "Network error. Please check your internet connection."
            msg.contains("sms") -> "Unable to send SMS right now. Please verify SMS provider settings."
            else -> "Authentication failed: ${error.localizedMessage ?: "Please try again."}"
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
