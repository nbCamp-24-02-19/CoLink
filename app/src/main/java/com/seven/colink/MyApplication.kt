package com.seven.colink

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        KakaoSdk.init(this,"5d721a804affa3a1e4e8b09839df568a")
        Log.d("hashKey", "${KakaoSdk.keyHash}")
        super.onCreate()
    }
}