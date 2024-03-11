package com.seven.colink.ui.post.register.post.model


sealed interface TagListItem {
    data class Item(
        val name: String?
    ) : TagListItem

    data class ContentItem(
        val name: String?
    ) : TagListItem
}

enum class TagListViewType {
    LIST_ITEM, CONTENT_ITEM, UNKNOWN
    ;

    companion object {
        fun from(ordinal: Int): TagListViewType = TagListViewType.values().find {
            it.ordinal == ordinal
        } ?: UNKNOWN
    }
}