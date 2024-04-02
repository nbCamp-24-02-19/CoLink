package com.seven.colink.ui.post.content.model

import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

sealed interface PostContentItem {
    data class Item(
        val key: String?,
        val authId: String?,
        val title: String?,
        val imageUrl: String,
        val groupType: GroupType?,
        val status: ProjectStatus?,
        val description: String?,
        val tags: List<String>?,
        val registeredDate: String?,
        val views: Int?,
        val like: Int?,
        val isLike: Boolean = false
    ) : PostContentItem

    data class RecruitItem(
        val key: String?,
        val recruit: RecruitInfo,
        val buttonUiState: ContentButtonUiState,
        val status: ProjectStatus?
    ) : PostContentItem

    data class MemberItem(
        val key: String?,
        val userInfo: UserEntity?
    ) : PostContentItem

    data class TitleItem(
        val titleRes: Int,
        val viewType: GroupContentViewType
    ) : PostContentItem

    data class SubTitleItem(
        val titleRes: Int
    ) : PostContentItem

    data class MessageItem(
        val message: String?
    ) : PostContentItem

    data class AdditionalInfo(
        val key: String?,
        val precautions: String?,
        val recruitInfo: String?,
        val startDate: String?,
        val endDate: String?
    ) : PostContentItem

    data class CommentTitle(
        val count: Int
    ) : PostContentItem
    data class CommentItem(
        val key: String,
        val name: String,
        val profile: String,
        val description: String,
        val registeredDate: String?,
        val authId: String?,
        val buttonUiState: Boolean,
    ) : PostContentItem

    data object CommentSendItem: PostContentItem
}