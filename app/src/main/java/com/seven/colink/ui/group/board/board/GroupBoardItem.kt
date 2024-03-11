package com.seven.colink.ui.group.board.board

import androidx.annotation.StyleRes
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

sealed interface GroupBoardItem {
    data class GroupItem(
        val key: String?,
        val authId: String?,
        val teamName: String?,
        val imageUrl: String?,
        val status: ProjectStatus,
        val groupType: GroupType,
        val description: String?,
        val tags: List<String>?,
        val startDate: String?,
        val endDate: String?,
        val isOwner: Boolean?,
    ) : GroupBoardItem

    data class PostItem(
        val post: Post
    ) : GroupBoardItem

    data class MemberItem(
        val userInfo: UserEntity,
    ) : GroupBoardItem

    data class MemberApplicationInfoItem(
        val userInfo: UserEntity,
        val applicationInfo: ApplicationInfo?
    ) : GroupBoardItem

    data class TitleSingleItem(
        val titleRes: Int,
    ) : GroupBoardItem

    data class TitleItem(
        val titleRes: Int,
        val viewType: GroupContentViewType,
        val buttonUiState: ContentButtonUiState
    ) : GroupBoardItem

    data class SubTitleItem(
        val title: String?,
        @StyleRes val style: Int = 0
    ) : GroupBoardItem

    data class MessageItem(
        val message: String?
    ) : GroupBoardItem

}
