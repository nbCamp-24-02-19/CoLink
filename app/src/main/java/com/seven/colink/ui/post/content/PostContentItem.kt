package com.seven.colink.ui.post.content

import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.util.status.GroupType

sealed interface PostContentItem {
    data class Item(
        val id: String?,
        val groupType: GroupType?,
        val title: String?,
        val tags: List<String>?,
        val datetime: String?,
        val content: String?
    ) : PostContentItem

    data class RecruitItem(
        val recruit: RecruitInfo
    ) : PostContentItem

    data class MemberItem(
        val userInfo: String
    ) : PostContentItem

    data class ImageItem(
        val imageUrl: String
    ) : PostContentItem
}

enum class PostContentViewType {
    ITEM,
    RECRUIT,
    MEMBER,
    IMAGE,
    UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): PostContentViewType = PostContentViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}