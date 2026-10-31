package com.haze.servicesprovider.domain.usecase

import com.haze.servicesprovider.domain.model.AuthState
import com.haze.servicesprovider.domain.repository.AuthRepository
import com.haze.servicesprovider.utils.AppConfig
import com.haze.servicesprovider.utils.ValidationUtils
import kotlinx.coroutines.flow.StateFlow

class CheckAuthStateUseCase(private val authRepository: AuthRepository) {
    val authState: StateFlow<AuthState> = authRepository.authState

    suspend operator fun invoke(): AuthState {
        return authRepository.checkInitialAuthState()
    }
}

class SendOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(rawPhoneNumber: String): Result<Unit> {
        val validation = ValidationUtils.validateIndianMobileNumber(rawPhoneNumber)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.errorMessage ?: "Invalid mobile number"))
        }
        val fullPhoneNumber = "${AppConfig.DEFAULT_COUNTRY_CODE}${validation.cleanedValue}"
        return authRepository.sendPhoneOtp(fullPhoneNumber)
    }
}

class VerifyOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(rawPhoneNumber: String, otp: String): Result<String> {
        val phoneValidation = ValidationUtils.validateIndianMobileNumber(rawPhoneNumber)
        if (!phoneValidation.isValid) {
            return Result.failure(IllegalArgumentException(phoneValidation.errorMessage ?: "Invalid mobile number"))
        }
        val otpValidation = ValidationUtils.validateOtp(otp)
        if (!otpValidation.isValid) {
            return Result.failure(IllegalArgumentException(otpValidation.errorMessage ?: "Invalid OTP"))
        }
        val fullPhoneNumber = "${AppConfig.DEFAULT_COUNTRY_CODE}${phoneValidation.cleanedValue}"
        return authRepository.verifyPhoneOtp(fullPhoneNumber, otpValidation.cleanedValue ?: otp)
    }
}

class ResendOtpUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(rawPhoneNumber: String): Result<Unit> {
        val validation = ValidationUtils.validateIndianMobileNumber(rawPhoneNumber)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.errorMessage ?: "Invalid mobile number"))
        }
        val fullPhoneNumber = "${AppConfig.DEFAULT_COUNTRY_CODE}${validation.cleanedValue}"
        return authRepository.resendOtp(fullPhoneNumber)
    }
}

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}

class DeleteAccountUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.deleteAccount()
    }
}
