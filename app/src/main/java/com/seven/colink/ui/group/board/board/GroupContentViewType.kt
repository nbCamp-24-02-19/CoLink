package com.seven.colink.ui.group.board.board

enum class GroupContentViewType {
    GROUP_ITEM, OPTION_ITEM, POST_ITEM, MEMBER_ITEM, APPLICATION_INFO, TITLE, SINGLE_TITLE, SUB_TITLE, MESSAGE, UNKNOWN,
    ;
    companion object {
        fun from(ordinal: Int): GroupContentViewType = GroupContentViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}