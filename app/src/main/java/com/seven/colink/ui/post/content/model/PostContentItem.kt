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
        val views: Int?
    ) : PostContentItem

    data class RecruitItem(
        val recruit: RecruitInfo,
        val buttonUiState: ContentOwnerButtonUiState
    ) : PostContentItem

    data class MemberItem(
        val userInfo: UserEntity
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
        val precautions: String?,
        val recruitInfo: String?
    ) : PostContentItem
}