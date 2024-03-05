package com.seven.colink.util.dialog.enum

import com.seven.colink.util.status.PostContentViewType

enum class RecommendViewType {
    TITLE,CARD,MIDDLE,OTHERS,CLOSE,UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): RecommendViewType = RecommendViewType.values().find {
            it.ordinal == ordinal
        } ?: RecommendViewType.UNKNOWN
    }
}