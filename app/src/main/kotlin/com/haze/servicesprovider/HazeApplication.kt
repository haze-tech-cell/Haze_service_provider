package com.haze.servicesprovider

import android.app.Application
import com.haze.servicesprovider.di.ServiceLocator

class HazeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.initialize(this)
    }
}
