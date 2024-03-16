package com.seven.colink.data.firebase.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.seven.colink.data.remote.dto.NotificationBody
import com.seven.colink.data.remote.retrofit.FcmRetrofit
import com.seven.colink.domain.repository.NotifyRepository
import javax.inject.Inject

class NotifyRepositoryImpl @Inject constructor(
    private val fcmRetrofit: FcmRetrofit
): NotifyRepository {
    suspend fun sendNotification(notification: NotificationBody) {
        fcmRetrofit.api.sendNotification(notification)
    }
}