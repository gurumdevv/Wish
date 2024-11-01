package com.gurumlab.wish.di

import com.gurumlab.wish.data.source.remote.ApiCallAdapterFactory
import com.gurumlab.wish.data.source.remote.ApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiClientModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @DatabaseOkhttpClient
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Singleton
    @DatabaseRetrofit
    @Provides
    fun provideRetrofit(@DatabaseOkhttpClient client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://wish-a3649-default-rtdb.asia-southeast1.firebasedatabase.app")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiServices(@DatabaseRetrofit retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseOkhttpClient