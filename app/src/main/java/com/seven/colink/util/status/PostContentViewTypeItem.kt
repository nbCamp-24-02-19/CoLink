package com.seven.colink.util.status

enum class PostContentViewTypeItem {
    ITEM,
    RECRUIT,
    MEMBER,
    TITLE,
    SUB_TITLE,
    UNKNOWN,
    MESSAGE,
    ADDITIONAL_INFO
    ;

    companion object {
        fun from(ordinal: Int): PostContentViewTypeItem = PostContentViewTypeItem.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}