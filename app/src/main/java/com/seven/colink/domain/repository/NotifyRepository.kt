package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.NotificationEntity

interface NotifyRepository {
    suspend fun sendNotification(notification: NotificationEntity)
}
