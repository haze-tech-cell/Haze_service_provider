package com.haze.servicesprovider.di

import android.annotation.SuppressLint
import android.content.Context
import com.haze.servicesprovider.data.local.SessionManager
import com.haze.servicesprovider.data.remote.SupabaseClientProvider
import com.haze.servicesprovider.data.repository.AuthRepositoryImpl
import com.haze.servicesprovider.data.repository.CategoryRepositoryImpl
import com.haze.servicesprovider.data.repository.LeadRepositoryImpl
import com.haze.servicesprovider.data.repository.ProfileRepositoryImpl
import com.haze.servicesprovider.domain.repository.AuthRepository
import com.haze.servicesprovider.domain.repository.CategoryRepository
import com.haze.servicesprovider.domain.repository.LeadRepository
import com.haze.servicesprovider.domain.repository.ProfileRepository
import com.haze.servicesprovider.domain.usecase.CheckAuthStateUseCase
import com.haze.servicesprovider.domain.usecase.DeleteAccountUseCase
import com.haze.servicesprovider.domain.usecase.GetCategoriesUseCase
import com.haze.servicesprovider.domain.usecase.GetLeadsUseCase
import com.haze.servicesprovider.domain.usecase.GetProfileUseCase
import com.haze.servicesprovider.domain.usecase.LogoutUseCase
import com.haze.servicesprovider.domain.usecase.ResendOtpUseCase
import com.haze.servicesprovider.domain.usecase.SaveProfileUseCase
import com.haze.servicesprovider.domain.usecase.SendOtpUseCase
import com.haze.servicesprovider.domain.usecase.UpdateLeadStatusUseCase
import com.haze.servicesprovider.domain.usecase.UploadProfilePhotoUseCase
import com.haze.servicesprovider.domain.usecase.VerifyOtpUseCase

@SuppressLint("StaticFieldLeak")
object ServiceLocator {

    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    val sessionManager: SessionManager by lazy {
        SessionManager(applicationContext)
    }

    val supabaseProvider: SupabaseClientProvider by lazy {
        SupabaseClientProvider()
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(supabaseProvider, sessionManager)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(supabaseProvider, sessionManager, profileRepository)
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(supabaseProvider)
    }

    val leadRepository: LeadRepository by lazy {
        LeadRepositoryImpl(supabaseProvider)
    }

    // Use cases
    val checkAuthStateUseCase: CheckAuthStateUseCase by lazy {
        CheckAuthStateUseCase(authRepository)
    }

    val sendOtpUseCase: SendOtpUseCase by lazy {
        SendOtpUseCase(authRepository)
    }

    val verifyOtpUseCase: VerifyOtpUseCase by lazy {
        VerifyOtpUseCase(authRepository)
    }

    val resendOtpUseCase: ResendOtpUseCase by lazy {
        ResendOtpUseCase(authRepository)
    }

    val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }

    val deleteAccountUseCase: DeleteAccountUseCase by lazy {
        DeleteAccountUseCase(authRepository)
    }

    val getProfileUseCase: GetProfileUseCase by lazy {
        GetProfileUseCase(profileRepository)
    }

    val saveProfileUseCase: SaveProfileUseCase by lazy {
        SaveProfileUseCase(profileRepository)
    }

    val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase by lazy {
        UploadProfilePhotoUseCase(profileRepository)
    }

    val getCategoriesUseCase: GetCategoriesUseCase by lazy {
        GetCategoriesUseCase(categoryRepository)
    }

    val getLeadsUseCase: GetLeadsUseCase by lazy {
        GetLeadsUseCase(leadRepository)
    }

    val updateLeadStatusUseCase: UpdateLeadStatusUseCase by lazy {
        UpdateLeadStatusUseCase(leadRepository)
    }
}
