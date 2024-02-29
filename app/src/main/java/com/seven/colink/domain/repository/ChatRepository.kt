package com.seven.colink.domain.repository

import com.google.firebase.database.DatabaseReference
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity

interface ChatRepository {

    suspend fun sendMessage(message: MessageEntity)
    suspend fun createChatRoom(chatRoom: ChatRoomEntity)
    suspend fun getChatRoom(chatRoomId: String): ChatRoomEntity?
    suspend fun observeMessages(chatRoom: ChatRoomEntity, callback: (List<MessageEntity>) -> Unit)
    suspend fun getChatRoomList(userId: String): Result<DatabaseReference>
    suspend fun deleteChatRoom(chatRoomId: String)
    suspend fun getChatRoomMessage(chatRoomId: String): List<MessageEntity>
}
