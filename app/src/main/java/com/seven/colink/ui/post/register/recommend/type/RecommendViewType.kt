package com.seven.colink.ui.post.register.recommend.type

enum class RecommendViewType {
    TITLE,CARD,MIDDLE,OTHERS,CLOSE,UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): RecommendViewType = RecommendViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}