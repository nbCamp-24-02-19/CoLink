package com.seven.colink.ui.post.register.post.model

import android.net.Uri
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.util.status.GroupType


sealed interface PostListItem {
    data class PostItem(
        val title: String?,
        val key: String?,
        val imageUrl: String?,
        val groupType: GroupType,
        val selectedImageUrl: Uri?,
        val description: String?,
        val descriptionMessage: String?,
        val tags: List<String>?,
        val registeredDate: String?,
        val view: Int?,
        val memberIds: List<String>,
    ) : PostListItem

    data class PostOptionItem(
        val key: String?,
        val precautions: String?,
        val recruitInfo: String?,
        val startDate: String?,
        val endDate: String?
    ) : PostListItem

    data class RecruitItem(
        val key: String?,
        val recruit: List<RecruitInfo>?,
        val groupType: GroupType?,
        val selectedCount: Int?
    ) : PostListItem

    data class TitleItem(
        val firstMessage: Int?,
        val secondMessage: Int? = null
    ) : PostListItem

    data class ButtonItem(
        val buttonText: Int?
    ) : PostListItem

}