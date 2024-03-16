package com.seven.colink.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AppInterceptor : Interceptor {
    companion object {
        const val FCM_URL = "https://fcm.googleapis.com"
        const val FCM_KEY = "AAAAmNfGnMw:APA91bGAFo88zg6OD4UBYUncsuWa9cRkb6GUAErLSGREOM3h7ILet9XAbl6pA8P9A-fbd4PD5No0Ii67eofLzyusgDB3RZB-8FnKnymCCOLOK-4HjswHk5oDB63WKYxaMvHWqNVGLi6w"
    }
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain)
            : Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader("Authorization", "key=$FCM_KEY")
            .addHeader("Content-Type", "application/json")
            .build()
        proceed(newRequest)
    }
}