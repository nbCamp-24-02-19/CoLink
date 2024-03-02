package com.seven.colink.ui.chat.model

sealed interface ChatRoomItem {
    data class MyMessage (
        val key: String,
        val text: String,
        val time: String,
        val viewCount: String,
    ): ChatRoomItem

    data class OtherMessage (
        val key: String,
        val name: String,
        val profileUrl: String?,
        val text: String,
        val time: String,
        val viewCount: String,
    ): ChatRoomItem
}