package com.seven.colink.util.status

enum class GroupViewType {
    TITLE,
    LIST,
    ADD,
    WANT,
    EMPTY,
    UNKNOWN;

    companion object {
        fun from(ordinal: Int): GroupViewType = GroupViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}