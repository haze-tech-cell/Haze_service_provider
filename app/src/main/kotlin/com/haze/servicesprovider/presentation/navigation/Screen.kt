package com.haze.servicesprovider.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object MobileRegistration : Screen("mobile_registration")

    data object OtpVerification : Screen("otp_verification/{phoneNumber}") {
        fun createRoute(phoneNumber: String): String = "otp_verification/$phoneNumber"
    }

    data object BusinessRegistration : Screen("business_registration/{phoneNumber}") {
        fun createRoute(phoneNumber: String): String = "business_registration/$phoneNumber"
    }

    data object RegistrationSuccess : Screen("registration_success")

    // Main 3 Bottom Navigation Tabs
    data object Home : Screen("home")
    data object Leads : Screen("leads")
    data object Settings : Screen("settings")

    // Profile Screens
    data object MyProfile : Screen("my_profile")
    data object EditProfile : Screen("edit_profile")

    // Settings Sub-screens
    data object NotificationSettings : Screen("notification_settings")
    data object HelpSupport : Screen("help_support")
    data object About : Screen("about")
    data object PrivacyPolicy : Screen("privacy_policy")
    data object TermsConditions : Screen("terms_conditions")
}
