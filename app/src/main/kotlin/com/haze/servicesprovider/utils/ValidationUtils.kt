package com.haze.servicesprovider.utils

object ValidationUtils {

    /**
     * Validates an Indian mobile number.
     * Expected format: exactly 10 digits starting with 6, 7, 8, or 9.
     */
    fun validateIndianMobileNumber(rawNumber: String): ValidationResult {
        val cleaned = rawNumber.trim().filter { it.isDigit() }
        val tenDigits = if (cleaned.startsWith("91") && cleaned.length == 12) {
            cleaned.substring(2)
        } else if (cleaned.startsWith("0") && cleaned.length == 11) {
            cleaned.substring(1)
        } else {
            cleaned
        }

        return when {
            tenDigits.isEmpty() -> ValidationResult(false, "Mobile number is required")
            tenDigits.length < 10 -> ValidationResult(false, "Enter full 10-digit mobile number")
            tenDigits.length > 10 -> ValidationResult(false, "Mobile number must not exceed 10 digits")
            !tenDigits.matches(Regex("^[6-9]\\d{9}$")) -> ValidationResult(
                false,
                "Enter a valid Indian mobile number starting with 6-9"
            )
            else -> ValidationResult(true, null, tenDigits)
        }
    }

    /**
     * Validates optional secondary mobile number.
     */
    fun validateSecondaryMobileNumber(rawNumber: String, primaryNumber: String = ""): ValidationResult {
        val trimmed = rawNumber.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult(true, null, "")
        }
        val result = validateIndianMobileNumber(trimmed)
        if (!result.isValid) {
            return result
        }
        if (result.cleanedValue == primaryNumber) {
            return ValidationResult(false, "Secondary number cannot be the same as primary number")
        }
        return result
    }

    /**
     * Validates full name.
     */
    fun validateFullName(name: String): ValidationResult {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Full name is required")
            trimmed.length < 2 -> ValidationResult(false, "Name must be at least 2 characters")
            trimmed.length > 60 -> ValidationResult(false, "Name must not exceed 60 characters")
            !trimmed.matches(Regex("^[a-zA-Z\\s.'-]+$")) -> ValidationResult(
                false,
                "Name can only contain alphabets and spaces"
            )
            else -> ValidationResult(true, null, trimmed)
        }
    }

    /**
     * Validates City name.
     */
    fun validateCity(city: String): ValidationResult {
        val trimmed = city.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "City is required")
            trimmed.length < 2 -> ValidationResult(false, "City must be at least 2 characters")
            else -> ValidationResult(true, null, trimmed)
        }
    }

    /**
     * Validates full address.
     */
    fun validateAddress(address: String): ValidationResult {
        val trimmed = address.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Full address is required")
            trimmed.length < 5 -> ValidationResult(false, "Address must be at least 5 characters")
            else -> ValidationResult(true, null, trimmed)
        }
    }

    /**
     * Validates OTP input.
     */
    fun validateOtp(otp: String): ValidationResult {
        val trimmed = otp.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "OTP is required")
            trimmed.length != 6 -> ValidationResult(false, "Please enter all 6 digits")
            !trimmed.all { it.isDigit() } -> ValidationResult(false, "OTP must contain digits only")
            else -> ValidationResult(true, null, trimmed)
        }
    }

    /**
     * Validates category selection.
     */
    fun validateCategory(category: String, customCategory: String): ValidationResult {
        val trimmedCategory = category.trim()
        if (trimmedCategory.isEmpty()) {
            return ValidationResult(false, "Please select a service category")
        }
        if (trimmedCategory.equals("Other", ignoreCase = true)) {
            val custom = customCategory.trim()
            if (custom.isEmpty()) {
                return ValidationResult(false, "Please enter your custom service category")
            }
            if (custom.length < 3) {
                return ValidationResult(false, "Category name must be at least 3 characters")
            }
        }
        return ValidationResult(true, null)
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val cleanedValue: String? = null
)
