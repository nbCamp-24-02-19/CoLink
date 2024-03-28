package com.seven.colink.util.status

enum class PostContentViewType {
    ITEM,
    OPTION_ITEM,
    GROUP_TYPE,
    RECRUIT,
    MEMBER,
    TITLE,
    SUB_TITLE,
    BUTTON_COMPLETE,
    UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): PostContentViewType = PostContentViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}