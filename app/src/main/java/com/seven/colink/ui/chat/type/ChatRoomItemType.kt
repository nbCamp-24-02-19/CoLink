package com.seven.colink.ui.chat.type

enum class ChatRoomItemType {
    MY, OTHER, UNKNOWN
    ;

    companion object{
        fun from(ordinal: Int) = ChatRoomItemType.values().find {
            it.ordinal == ordinal
        }?: UNKNOWN
    }
}