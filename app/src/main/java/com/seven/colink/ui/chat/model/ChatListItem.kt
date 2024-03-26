package com.seven.colink.ui.chat.model

data class ChatListItem(
    val key: String,
    val title: String,
    val message: String,
    val thumbnail: String?,
    val recentTime: String?,
    val unreadCount: Int?,
)
