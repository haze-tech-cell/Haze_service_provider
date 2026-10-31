package com.haze.servicesprovider.utils

import com.haze.servicesprovider.BuildConfig

object AppConfig {
    val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
        .trim()
        .removeSuffix("/rest/v1/")
        .removeSuffix("/rest/v1")
        .removeSuffix("/")
        .ifBlank {
            "https://placeholder-project.supabase.co"
        }

    val SUPABASE_ANON_KEY: String = BuildConfig.SUPABASE_ANON_KEY.trim().ifBlank {
        "placeholder-anon-key"
    }

    const val PROFILE_STORAGE_BUCKET = "profile-photos"
    const val DEFAULT_COUNTRY_CODE = "+91"
    const val OTP_LENGTH = 6
    const val RESEND_OTP_TIMEOUT_SECONDS = 30
}
