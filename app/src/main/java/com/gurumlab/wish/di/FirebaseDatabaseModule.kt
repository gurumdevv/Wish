package com.gurumlab.wish.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseDatabaseModule {

    @ViewModelScoped
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @ViewModelScoped
    @Provides
    fun provideDatabaseReference(database: FirebaseDatabase): DatabaseReference {
        return database.reference
    }
}