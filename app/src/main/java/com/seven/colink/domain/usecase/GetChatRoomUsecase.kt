package com.seven.colink.domain.usecase

import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.ui.chat.type.ChatTabType
import javax.inject.Inject

class GetChatRoomUsecase @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(
        uid: String,
        type: ChatTabType = ChatTabType.GENERAL
    ): ChatRoomEntity {
        val currentUid = authRepository.getCurrentUser().message
        val newChat = ChatRoomEntity(
            key = "CR_" + listOf(uid, currentUid).sorted().joinToString(""),
            participantsUid = listOf(uid, currentUid),
            type = type
        )
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
        type: ChatTabType = ChatTabType.GENERAL,
        thumbnail: String?
    ): ChatRoomEntity {
        val newChat = ChatRoomEntity(
            key = "CR_$key",
            title = title,
            participantsUid = uids,
            thumbnail = thumbnail,
            type = type
        )
        val chatRoom = chatRepository.getChatRoom(newChat.key)
        return if (chatRoom == null) {
            chatRepository.createChatRoom(newChat)
            newChat
        } else {
            chatRoom
        }
    }
}