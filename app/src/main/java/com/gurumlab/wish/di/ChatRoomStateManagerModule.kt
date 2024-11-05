package com.gurumlab.wish.di

import com.gurumlab.wish.ui.message.ChatRoomStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatRoomStateManagerModule {

    @Provides
    @Singleton
    fun getChatRoomStateManager(): ChatRoomStateManager {
        return ChatRoomStateManager()
    }
}