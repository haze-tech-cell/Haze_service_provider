package com.haze.servicesprovider

import android.app.Application
import android.content.Context
import android.util.Log
import com.haze.servicesprovider.di.ServiceLocator

class HazeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("HazeApplication", "CRASH in thread ${thread.name}: ${throwable.message}", throwable)
            try {
                val prefs = getSharedPreferences("haze_crash_store", Context.MODE_PRIVATE)
                prefs.edit().putString("last_crash", "${throwable.javaClass.simpleName}: ${throwable.message}\n${throwable.stackTraceToString().take(600)}").commit()
            } catch (_: Exception) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }

        ServiceLocator.initialize(this)
    }
}
