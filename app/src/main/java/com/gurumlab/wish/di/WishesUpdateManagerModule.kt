package com.gurumlab.wish.di

import com.gurumlab.wish.ui.util.WishesUpdateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WishesUpdateManagerModule {

    @Provides
    @Singleton
    fun provideWishesUpdateManager() = WishesUpdateManager()
}