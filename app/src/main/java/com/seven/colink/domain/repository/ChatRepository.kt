package com.seven.colink.domain.repository

import com.google.firebase.database.DatabaseReference
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.ui.chat.type.ChatTabType

interface ChatRepository {

    suspend fun sendMessage(message: MessageEntity)
    suspend fun createChatRoom(chatRoom: ChatRoomEntity)
    suspend fun getChatRoom(chatRoomId: String): ChatRoomEntity?
    suspend fun observeMessages(chatRoom: ChatRoomEntity, callback: (List<MessageEntity>) -> Unit)
    suspend fun deleteChatRoom(chatRoomId: String)
    suspend fun getChatRoomList(userId: String, type: ChatTabType): Result<List<ChatRoomEntity>>
    suspend fun getChatRoomMessage(chatRoomId: String): List<MessageEntity>?
}
