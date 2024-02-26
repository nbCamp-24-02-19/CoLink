package com.seven.colink.domain.repository

import com.google.firebase.database.DatabaseReference
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity

interface ChatRepository {

    suspend fun sendMessage(message: MessageEntity)
    suspend fun createChatRoom(chatRoom: ChatRoomEntity)
    suspend fun getChatRoom(chatRoomId: String): Any?
    suspend fun observeMessages(chatRoom: ChatRoomEntity, callback: (List<MessageEntity>) -> Unit)
    suspend fun getChatRoomList(userId: String): Result<DatabaseReference>
}
