package com.seven.colink.ui.notify.type

enum class NotifyType {
    FILTER,CHAT,DEFAULT
    ;

    companion object {
        fun from(ordinal: Int) = entries.find {
            it.ordinal == ordinal
        } ?: DEFAULT
    }
}