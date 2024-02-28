package com.seven.colink.ui.post

import com.seven.colink.domain.entity.TagEntity

sealed interface TagListItem {
    data class Item(
        val tagEntity: TagEntity?
    ) : TagListItem {
        val key: String? get() = tagEntity?.key
    }

    data class ContentItem(
        val tagName: String?
    ) : TagListItem
}

enum class TagListViewType {
    LIST_ITEM,
    CONTENT_ITEM,
    UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): TagListViewType = TagListViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}

sealed class AddTagResult {
    data object Success : AddTagResult()
    data object MaxNumberExceeded : AddTagResult()
    data object TagAlreadyExists : AddTagResult()
}