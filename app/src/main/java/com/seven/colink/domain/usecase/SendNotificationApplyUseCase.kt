package com.seven.colink.domain.usecase

import com.seven.colink.R
import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.domain.model.NotifyType
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.NotificationStoreRepository
import com.seven.colink.domain.repository.NotifyRepository
import com.seven.colink.domain.repository.ResourceRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.post.register.post.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendNotificationApplyUseCase @Inject constructor(
    private val notifyRepository: NotifyRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val resourceRepository: ResourceRepository,
    private val notificationStoreRepository: NotificationStoreRepository,
) {
    suspend operator fun invoke(data: Post) = withContext(Dispatchers.IO) {
        userRepository.getUserDetails(authRepository.getCurrentUser().message).getOrNull()
            .let { currentUser ->
                userRepository.getUserDetails(data.authId!!).getOrNull().let { auth ->
                    NotificationEntity(
                        key = data.key,
                        toUserToken = auth?.token,
                        toUserId = auth?.uid,
                        type = NotifyType.APPLY,
                        title = resourceRepository.getString(R.string.notify_new_apply),
                        message = resourceRepository.getString(
                            R.string.group_apply_message,
                            data.title!!,
                            currentUser!!.name!!
                        ),
                        groupType = data.groupType
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