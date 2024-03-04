package com.seven.colink.ui.chat.type

sealed interface MessageType{
    data class MyMessage(
        val key: String?,
        val text: String?,
        val viewUser: List<String> = emptyList(),
        val registerTime: String,
    )

    data class OtherMessage(
        val key: String?,
        val authId: String?,
        val profileImg: String?,
        val text: String?,
        val viewUser: List<String> = emptyList(),
        val registerTime: String,
    )
}

enum class MessageState {
    DEFAULT, FIRST, MIDDLE, LAST
}