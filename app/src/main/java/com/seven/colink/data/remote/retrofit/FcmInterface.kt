package com.seven.colink.data.remote.retrofit

import com.seven.colink.data.remote.dto.NotificationDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmInterface {
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: NotificationDTO
    ) : Response<ResponseBody>
}