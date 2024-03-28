package com.seven.colink.di

import android.content.Context
import com.kakao.sdk.user.UserApiClient
import com.seven.colink.data.remote.repository.KakaoRepositoryImpl
import com.seven.colink.domain.repository.KakaoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object KaKaoModule {
    @Provides
    fun providesKaKaoRepository(context: Context): KakaoRepository
    = KakaoRepositoryImpl(UserApiClient.instance , context)
}