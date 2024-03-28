package com.seven.colink.ui.chat.model

import com.seven.colink.util.model.UrlMetaData

sealed interface ChatRoomItem {
    data class MyMessage (
        val key: String,
        val text: String? = null,
        val img: String? = null,
        val time: String,
        val viewCount: Int,
        val embed: UrlMetaData? = null,
    ): ChatRoomItem

    data class OtherMessage (
        val key: String,
        val name: String,
        val profileUrl: String?,
        val text: String? = null,
        val img: String? = null,
        val time: String,
        val viewCount: Int,
        val embed: UrlMetaData? = null,
    ): ChatRoomItem
}