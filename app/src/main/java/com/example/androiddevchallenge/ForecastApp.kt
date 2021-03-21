package com.example.androiddevchallenge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ForecastApp:Application() {
    override fun onCreate() {
        super.onCreate()
    }
}