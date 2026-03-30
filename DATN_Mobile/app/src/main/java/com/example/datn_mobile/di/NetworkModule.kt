package com.example.datn_mobile.di

import com.example.datn_mobile.data.network.api.AuthApiService
import com.example.datn_mobile.data.network.api.CartApiService
import com.example.datn_mobile.data.network.api.ProductApiService
import com.example.datn_mobile.data.network.api.UserApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideUAuthApi(retrofit: Retrofit) : AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit) : UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit) : ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCartApi(retrofit: Retrofit) : CartApiService {
        return retrofit.create(CartApiService::class.java)
    }
}