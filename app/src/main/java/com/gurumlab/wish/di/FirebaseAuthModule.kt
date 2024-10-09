package com.gurumlab.wish.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gurumlab.wish.data.auth.FirebaseAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseAuthModule {

    @ViewModelScoped
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @ViewModelScoped
    @Provides
    fun provideAuthCurrentUser(auth: FirebaseAuth): FirebaseUser? = auth.currentUser

    @ViewModelScoped
    @Provides
    fun provideAuthManager(currentUser: FirebaseUser?): FirebaseAuthManager =
        FirebaseAuthManager(currentUser)
}