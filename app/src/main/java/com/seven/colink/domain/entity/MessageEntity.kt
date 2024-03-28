package com.seven.colink.domain.entity

import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.model.UrlMetaData
import java.time.LocalDateTime
import java.util.UUID

data class ChatRoomEntity(
    val key: String = "CR_" + UUID.randomUUID().toString(),
    val title: String? = "",
    val participantsUid: Map<String, Boolean> = emptyMap(),
    val thumbnail: String? = "",
    val type: ChatTabType? = ChatTabType.GENERAL,
    val registerDate: String = LocalDateTime.now().convertLocalDateTime(),
)
data class MessageEntity (
    val key: String = "MSG_" + System.currentTimeMillis().toString() + "_" + UUID.randomUUID().toString(),
    val chatRoomId: String = "",
    val text: String? = null,
    val authId: String? = "",
    val img: String? = null,
    val embed: UrlMetaData? = null,
    val viewUsers: List<String> = emptyList(),
    val registerDate: String = LocalDateTime.now().convertLocalDateTime(),
)
