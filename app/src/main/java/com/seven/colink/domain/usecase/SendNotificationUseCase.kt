package com.seven.colink.domain.usecase

import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.domain.model.NotifyType
import com.seven.colink.domain.repository.NotifyRepository
import com.seven.colink.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendNotificationUseCase @Inject constructor(
    private val notifyRepository: NotifyRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(data: MessageEntity, chatRoom: ChatRoomEntity) =
        withContext(Dispatchers.IO) {
            userRepository.getUserDetails(data.authId!!).getOrNull().let { currentUser ->
                chatRoom.participantsUid.forEach { (uid, _) ->
                    if (uid != data.authId) {
                        userRepository.getUserDetails(uid).getOrNull().let { userEntity ->
                            notifyRepository.sendNotification(
                                NotificationEntity(
                                    toUserToken = userEntity?.token,
                                    message = data.text,
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
                                )
                            )
                        }
                    }
                }
            }
        }
}