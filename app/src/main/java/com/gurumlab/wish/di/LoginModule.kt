package com.gurumlab.wish.di

import com.gurumlab.wish.ui.settings.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
object LoginModule {

    @ViewModelScoped
    @Provides
    fun provideLoginService(): LoginService {
        return LoginService()
    }
}