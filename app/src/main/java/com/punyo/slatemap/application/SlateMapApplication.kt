package com.punyo.slatemap.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SlateMapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
