package com.tm.netomichatapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NetomiChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}