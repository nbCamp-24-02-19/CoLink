package com.seven.colink.di

import dagger.Provides
import com.seven.colink.data.remote.retrofit.FcmInterface
import com.seven.colink.data.remote.retrofit.FcmRetrofit
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Singleton
    @Provides
    fun provideCloudMessageService(): FcmInterface =
        FcmRetrofit.fcmNetwork
}