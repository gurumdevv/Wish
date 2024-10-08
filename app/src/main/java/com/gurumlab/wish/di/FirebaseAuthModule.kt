package com.gurumlab.wish.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gurumlab.wish.data.auth.FirebaseAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideAuthCurrentUser(auth: FirebaseAuth): FirebaseUser? = auth.currentUser

    @Singleton
    @Provides
    fun provideAuthManager(currentUser: FirebaseUser?): FirebaseAuthManager =
        FirebaseAuthManager(currentUser)
}