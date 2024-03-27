package com.seven.colink.data.remote.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object KakaoRetrofit {
    private const val KAKAO_URL = "https://kauth.kakao.com/oauth/authorize"

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(KAKAO_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val kakaoNetwork: KakaoInterface =
        retrofit.create(KakaoInterface::class.java)
    private fun provideOkHttpClient(
        interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor)
            build()
        }
}