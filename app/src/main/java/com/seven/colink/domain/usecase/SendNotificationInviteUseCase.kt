package com.seven.colink.domain.usecase

import android.content.Context
import com.seven.colink.R
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.model.NotifyType
import com.seven.colink.domain.repository.NotificationStoreRepository
import com.seven.colink.domain.repository.NotifyRepository
import com.seven.colink.domain.repository.ResourceRepository
import com.seven.colink.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendNotificationInviteUseCase @Inject constructor(
    private val notifyRepository: NotifyRepository,
    private val userRepository: UserRepository,
    private val resourceRepository: ResourceRepository,
    private val notificationStoreRepository: NotificationStoreRepository,
) {
    suspend operator fun invoke(data: PostEntity, uid: String) =
        withContext(Dispatchers.IO) {
            userRepository.getUserDetails(uid).getOrNull()?.let { user ->
                NotificationEntity(
                    key = data.key,
                    toUserToken = user.token,
                    toUserId = user.uid,
                    message = resourceRepository.getString(R.string.group_invitation_message, data.title!!),
                    title = resourceRepository.getString(R.string.group_invitation_title),
                    type = NotifyType.INVITE,
                ).let { notificationEntity ->
                    notifyRepository.sendNotification(
                        notificationEntity
                    )
                    notificationStoreRepository.registerNotification(
                        notificationEntity
                    )
                }
            }
        }
}