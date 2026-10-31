package com.haze.servicesprovider

import android.app.Application
import android.util.Log
import com.haze.servicesprovider.di.ServiceLocator

class HazeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("HazeApplication", "CRASH in thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        ServiceLocator.initialize(this)
    }
}
