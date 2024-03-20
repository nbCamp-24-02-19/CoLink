package com.seven.colink.domain.usecase

import android.content.Context
import com.seven.colink.R
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.model.NotifyType
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.NotificationStoreRepository
import com.seven.colink.domain.repository.NotifyRepository
import com.seven.colink.domain.repository.ResourceRepository
import com.seven.colink.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendNotificationUseCase @Inject constructor(
    private val notifyRepository: NotifyRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val resourceRepository: ResourceRepository,
    private val notificationStoreRepository: NotificationStoreRepository,
) {
    suspend operator fun invoke(data: MessageEntity, chatRoom: ChatRoomEntity) =
        withContext(Dispatchers.IO) {
            userRepository.getUserDetails(data.authId!!).getOrNull().let { currentUser ->
                chatRoom.participantsUid.forEach { (uid, _) ->
                    if (uid != data.authId) {
                        userRepository.getUserDetails(uid).getOrNull().let { userEntity ->
                            NotificationEntity(
                                key = chatRoom.key,
                                toUserToken = userEntity?.token,
                                toUserId = userEntity?.uid,
                                message = data.text?: "사진",
                                name = currentUser?.name,
                                title = if (chatRoom.title.isNullOrEmpty()
                                        .not()
                                ) chatRoom.title else currentUser?.name,
                                type = NotifyType.CHAT,
                                thumbnail = if (chatRoom.thumbnail.isNullOrEmpty().not())
                                    chatRoom.thumbnail
                                else {
                                    currentUser?.photoUrl
                                }
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
            }
        }


    suspend operator fun invoke(data: PostEntity, uid: String) = withContext(Dispatchers.IO) {
            userRepository.getUserDetails(uid).getOrNull().let { auth ->
                NotificationEntity(
                    key = data.key,
                    toUserToken = auth?.token,
                    toUserId = auth?.uid,
                    message = resourceRepository.getString(R.string.group_join_message, data.title!!),
                    title = resourceRepository.getString(R.string.notify_join_group),
                    type = NotifyType.APPLY
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