package com.gurumlab.wish.di

import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseStorageModule {

    @Singleton
    @Provides
    fun provideStorage() = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideStorageRef(storage: FirebaseStorage) = storage.reference
}