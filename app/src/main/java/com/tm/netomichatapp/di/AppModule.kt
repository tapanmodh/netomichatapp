package com.tm.netomichatapp.di

import android.content.Context
import com.tm.netomichatapp.utils.ConnectivityObserver
import com.tm.netomichatapp.utils.ConnectivityObserverImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {

        return ConnectivityObserverImpl(context)
    }
}