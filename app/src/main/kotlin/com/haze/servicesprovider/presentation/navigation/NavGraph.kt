package com.haze.servicesprovider.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.haze.servicesprovider.presentation.auth.AuthViewModel
import com.haze.servicesprovider.presentation.auth.MobileRegistrationScreen
import com.haze.servicesprovider.presentation.auth.OtpVerificationScreen
import com.haze.servicesprovider.presentation.home.HomeScreen
import com.haze.servicesprovider.presentation.home.HomeViewModel
import com.haze.servicesprovider.presentation.leads.LeadsScreen
import com.haze.servicesprovider.presentation.leads.LeadsViewModel
import com.haze.servicesprovider.presentation.profile.EditProfileScreen
import com.haze.servicesprovider.presentation.profile.MyProfileScreen
import com.haze.servicesprovider.presentation.profile.ProfileViewModel
import com.haze.servicesprovider.presentation.registration.BusinessRegistrationScreen
import com.haze.servicesprovider.presentation.registration.BusinessRegistrationViewModel
import com.haze.servicesprovider.presentation.registration.RegistrationSuccessScreen
import com.haze.servicesprovider.presentation.settings.AboutScreen
import com.haze.servicesprovider.presentation.settings.HelpSupportScreen
import com.haze.servicesprovider.presentation.settings.NotificationSettingsScreen
import com.haze.servicesprovider.presentation.settings.PrivacyPolicyScreen
import com.haze.servicesprovider.presentation.settings.SettingsScreen
import com.haze.servicesprovider.presentation.settings.SettingsViewModel
import com.haze.servicesprovider.presentation.settings.TermsConditionsScreen
import com.haze.servicesprovider.presentation.splash.SplashScreen

@Composable
fun HazeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Shared ViewModels for scoped flows
    val authViewModel: AuthViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val leadsViewModel: LeadsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMobileRegistration = {
                    navController.navigate(Screen.MobileRegistration.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToCompleteRegistration = { phone ->
                    navController.navigate(Screen.BusinessRegistration.createRoute(phone)) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Mobile Number Registration Screen
        composable(Screen.MobileRegistration.route) {
            MobileRegistrationScreen(
                onNavigateToOtp = { phone ->
                    navController.navigate(Screen.OtpVerification.createRoute(phone))
                },
                onNavigateToTerms = {
                    navController.navigate(Screen.TermsConditions.route)
                },
                onNavigateToPrivacy = {
                    navController.navigate(Screen.PrivacyPolicy.route)
                },
                viewModel = authViewModel
            )
        }

        // OTP Verification Screen
        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber").orEmpty()
            OtpVerificationScreen(
                phoneNumber = phoneNumber,
                onNavigateBack = { navController.popBackStack() },
                onVerificationSuccess = {
                    // Check if profile was already complete from previous login
                    val profile = com.haze.servicesprovider.di.ServiceLocator.sessionManager.cachedProfile.value
                    if (profile != null && profile.profileCompleted) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.MobileRegistration.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.BusinessRegistration.createRoute(phoneNumber)) {
                            popUpTo(Screen.MobileRegistration.route) { inclusive = true }
                        }
                    }
                },
                viewModel = authViewModel
            )
        }

        // Business Registration Form Screen
        composable(
            route = Screen.BusinessRegistration.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber").orEmpty()
            val registrationViewModel: BusinessRegistrationViewModel = viewModel()
            BusinessRegistrationScreen(
                phoneNumber = phoneNumber,
                onRegistrationSuccess = {
                    navController.navigate(Screen.RegistrationSuccess.route) {
                        popUpTo(Screen.BusinessRegistration.route) { inclusive = true }
                    }
                },
                viewModel = registrationViewModel
            )
        }

        // Registration Success Screen
        composable(Screen.RegistrationSuccess.route) {
            RegistrationSuccessScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.RegistrationSuccess.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Tab 1: Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfile = { navController.navigate(Screen.MyProfile.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToLeads = {
                    navController.navigate(Screen.Leads.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNotificationClick = { navController.navigate(Screen.NotificationSettings.route) },
                viewModel = homeViewModel
            )
        }

        // Main Tab 2: Leads Screen
        composable(Screen.Leads.route) {
            LeadsScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                viewModel = leadsViewModel
            )
        }

        // Main Tab 3: Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToLeads = {
                    navController.navigate(Screen.Leads.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToProfile = { navController.navigate(Screen.MyProfile.route) },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToNotifications = { navController.navigate(Screen.NotificationSettings.route) },
                onNavigateToHelp = { navController.navigate(Screen.HelpSupport.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) },
                onNavigateToTerms = { navController.navigate(Screen.TermsConditions.route) },
                onLoggedOut = {
                    navController.navigate(Screen.MobileRegistration.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = settingsViewModel
            )
        }

        // My Profile Screen
        composable(Screen.MyProfile.route) {
            MyProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                viewModel = profileViewModel
            )
        }

        // Edit Profile Screen
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = profileViewModel
            )
        }

        // Notification Settings Screen
        composable(Screen.NotificationSettings.route) {
            NotificationSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = settingsViewModel
            )
        }

        // Help & Support Screen
        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // About Screen
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Privacy Policy Screen
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Terms and Conditions Screen
        composable(Screen.TermsConditions.route) {
            TermsConditionsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
