package com.tm.netomichatapp.utils

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val isInternetConnected: Flow<Boolean>
}