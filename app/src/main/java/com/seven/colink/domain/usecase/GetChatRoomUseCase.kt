package com.seven.colink.domain.usecase

import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.chat.type.ChatTabType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetChatRoomUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(
        uid: String,
        type: ChatTabType = ChatTabType.GENERAL
    ): ChatRoomEntity {
        val currentUid = authRepository.getCurrentUser().message
        val newChat = ChatRoomEntity(
            key = "CR_" + listOf(uid, currentUid).sorted().joinToString(""),
            participantsUid = mapOf(uid to true, currentUid to true),
            type = type
        )
        CoroutineScope(Dispatchers.IO).launch {
            val userDeferred = async { userRepository.getUserDetails(uid) }
            val myDeferred = async { userRepository.getUserDetails(currentUid) }

            userDeferred.await().onSuccess {
                if (it != null && it.participantsChatRoomIds?.contains(newChat.key)?.not() != false) {
                    userRepository.registerUser(
                        it.copy(
                            participantsChatRoomIds = it.participantsChatRoomIds?.plus(
                                newChat.key
                            )
                        )
                    )
                }
            }
            myDeferred.await().onSuccess {
                if (it != null && it.participantsChatRoomIds?.contains(newChat.key)?.not() != false) {
                    userRepository.registerUser(
                        it.copy(
                            participantsChatRoomIds = it.participantsChatRoomIds?.plus(
                                newChat.key
                            )
                        )
                    )
                }
            }
            this.cancel()
        }
        val chatRoom = chatRepository.getChatRoom(newChat.key)
        return if (chatRoom == null) {
            chatRepository.createChatRoom(newChat)
            newChat
        } else {
            chatRoom
        }
    }

    suspend operator fun invoke(
        key: String,
        uids: List<String>,
        title: String,
        type: ChatTabType = ChatTabType.PROJECT,
        thumbnail: String?
    ): ChatRoomEntity {
        val newChat = ChatRoomEntity(
            key = "CR_$key",
            title = title,
            participantsUid = uids.associateWith { true },
            thumbnail = thumbnail,
            type = type
        )
        CoroutineScope(Dispatchers.IO).launch {
            uids.forEach {
                launch {
                    userRepository.getUserDetails(it).getOrNull().let {
                        if (it != null && it.participantsChatRoomIds?.contains(newChat.key)
                                ?.not() != false
                        )
                            it.copy(
                                participantsChatRoomIds = it.participantsChatRoomIds?.plus(key)
                            )
                                .let { it1 -> userRepository.registerUser(it1) }
                    }

                }
            }
            chatRepository.createChatRoom(newChat)
            this.cancel()
        }
        return chatRepository.getChatRoom(newChat.key) ?: newChat
    }
}