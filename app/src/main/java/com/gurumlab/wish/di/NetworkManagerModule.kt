package com.gurumlab.wish.di

import android.content.Context
import com.gurumlab.wish.ui.util.NetWorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkManagerModule {

    @Singleton
    @Provides
    fun getNetworkManager(@ApplicationContext context: Context): NetWorkManager {
        return NetWorkManager(context)
    }
}