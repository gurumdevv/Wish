package com.gurumlab.wish.di

import android.content.Context
import com.gurumlab.wish.util.MessagingUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MessagingUtilModule {

    @Provides
    @Singleton
    fun provideMessagingUtil(@ApplicationContext context: Context): MessagingUtil {
        return MessagingUtil(context)
    }
}