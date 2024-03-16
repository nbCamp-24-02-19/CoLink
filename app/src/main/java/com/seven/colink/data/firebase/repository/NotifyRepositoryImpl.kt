package com.seven.colink.data.firebase.repository

import com.seven.colink.data.remote.dto.NotificationDTO
import com.seven.colink.data.remote.retrofit.FcmInterface
import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.domain.repository.NotifyRepository
import javax.inject.Inject

class NotifyRepositoryImpl @Inject constructor(
    private val fcmInterface: FcmInterface
): NotifyRepository {
    override suspend fun sendNotification(notification: NotificationEntity) {
        fcmInterface.sendNotification(notification.convert())
    }

    private fun NotificationEntity.convert() = NotificationDTO(
        to = toUserToken,
        data = NotificationDTO.NotificationData(
            title = title,
            type = type?.title,
            name = name,
            message = message,
            registeredDate = registeredDate,
            img = thumbnail,
        )
    )
}