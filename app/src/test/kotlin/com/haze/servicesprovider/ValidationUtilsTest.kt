package com.haze.servicesprovider

import com.haze.servicesprovider.utils.ValidationUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun testValidIndianMobileNumber() {
        val result = ValidationUtils.validateIndianMobileNumber("9876543210")
        assertTrue(result.isValid)
        assertEquals("9876543210", result.cleanedValue)
    }

    @Test
    fun testIndianMobileNumberWithPrefix() {
        val result = ValidationUtils.validateIndianMobileNumber("+919876543210")
        assertTrue(result.isValid)
        assertEquals("9876543210", result.cleanedValue)
    }

    @Test
    fun testInvalidIndianMobileNumber_StartsWrong() {
        val result = ValidationUtils.validateIndianMobileNumber("5555555555")
        assertFalse(result.isValid)
    }

    @Test
    fun testInvalidIndianMobileNumber_TooShort() {
        val result = ValidationUtils.validateIndianMobileNumber("98765")
        assertFalse(result.isValid)
    }

    @Test
    fun testFullNameValidation() {
        assertTrue(ValidationUtils.validateFullName("Ramesh Kumar").isValid)
        assertFalse(ValidationUtils.validateFullName("").isValid)
        assertFalse(ValidationUtils.validateFullName("A").isValid)
    }

    @Test
    fun testCityValidation() {
        assertTrue(ValidationUtils.validateCity("Mumbai").isValid)
        assertFalse(ValidationUtils.validateCity("").isValid)
    }

    @Test
    fun testAddressValidation() {
        assertTrue(ValidationUtils.validateAddress("Shop 4, Linking Road, Bandra West").isValid)
        assertFalse(ValidationUtils.validateAddress("Shop").isValid)
    }

    @Test
    fun testOtpValidation() {
        assertTrue(ValidationUtils.validateOtp("123456").isValid)
        assertFalse(ValidationUtils.validateOtp("12345").isValid)
        assertFalse(ValidationUtils.validateOtp("12345a").isValid)
    }

    @Test
    fun testCategoryValidation() {
        assertTrue(ValidationUtils.validateCategory("Plumber", "").isValid)
        assertFalse(ValidationUtils.validateCategory("", "").isValid)
        assertTrue(ValidationUtils.validateCategory("Other", "Solar Inverter Specialist").isValid)
        assertFalse(ValidationUtils.validateCategory("Other", "").isValid)
    }
}
