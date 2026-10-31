package com.haze.servicesprovider

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.haze.servicesprovider.di.ServiceLocator

class HazeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val errorText = buildString {
                append("Exception: ").append(throwable.javaClass.name).append("\n")
                append("Message: ").append(throwable.message ?: "No message").append("\n")
                append("Thread: ").append(thread.name).append("\n\n")
                append("Stacktrace:\n")
                append(throwable.stackTraceToString().take(2000))
            }
            Log.e("HazeCrash", errorText, throwable)

            try {
                val prefs = getSharedPreferences("haze_debug_store", Context.MODE_PRIVATE)
                prefs.edit().putString("last_crash", errorText).commit()

                val intent = Intent(this, CrashReportActivity::class.java).apply {
                    putExtra(CrashReportActivity.EXTRA_CRASH_DETAILS, errorText)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("HazeCrash", "Failed to start CrashReportActivity", e)
            }

            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }

        ServiceLocator.initialize(this)
    }
}
