package com.seven.colink.util.status

enum class PostContentViewType {
    ITEM,
    GROUP_TYPE,
    RECRUIT,
    MEMBER,
    IMAGE,
    TITLE,
    SUB_TITLE,
    UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): PostContentViewType = PostContentViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}