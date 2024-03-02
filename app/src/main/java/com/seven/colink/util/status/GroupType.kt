package com.seven.colink.util.status

enum class GroupType {
    PROJECT, STUDY, UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): GroupType = GroupType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}
