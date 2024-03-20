package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.util.status.DataResultStatus

interface NotificationStoreRepository {

    suspend fun registerNotification(notification: NotificationEntity)
    suspend fun deleteNotification(key: String): DataResultStatus
    suspend fun getNotificationByUid(uid: String): List<NotificationEntity>
}
