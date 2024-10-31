package com.gurumlab.wish.di

import com.gurumlab.wish.BuildConfig
import com.gurumlab.wish.data.source.remote.ApiCallAdapterFactory

import com.gurumlab.wish.data.source.remote.FCMApiClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FCMApiClientModule {

    @Singleton
    @FCMOkhttpClient
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val header = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BuildConfig.GOOGLE_SDK_KEY}")
                .build()
            chain.proceed(newRequest)
        }

        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(header)
            .build()
    }

    @Singleton
    @FCMRetrofit
    @Provides
    fun provideRetrofit(@FCMOkhttpClient client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiServices(@FCMRetrofit retrofit: Retrofit): FCMApiClient {
        return retrofit.create(FCMApiClient::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FCMRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FCMOkhttpClient