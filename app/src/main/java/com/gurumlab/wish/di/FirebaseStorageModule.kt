package com.gurumlab.wish.di

import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseStorageModule {

    @ViewModelScoped
    @Provides
    fun provideStorage() = FirebaseStorage.getInstance()

    @ViewModelScoped
    @Provides
    fun provideStorageRef(storage: FirebaseStorage) = storage.reference
}