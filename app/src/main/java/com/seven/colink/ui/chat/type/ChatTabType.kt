package com.seven.colink.ui.chat.type
enum class ChatTabType {
    GENERAL,
    PROJECT,
    STUDY
    ;

    companion object {
        fun getEntryType(ordinal: Int?): ChatTabType =
            ChatTabType.values().firstOrNull {
                it.ordinal == ordinal
            } ?: GENERAL
    }
}