package com.seven.colink.data.remote.retrofit

import com.seven.colink.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AppInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain)
            : Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader("Authorization", "key=${BuildConfig.FCM_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        proceed(newRequest)
    }
}