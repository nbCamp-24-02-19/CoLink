package com.seven.colink.ui.post.register.post.model

import android.net.Uri
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.util.status.GroupType

data class PostUiState(
    val title: String?,
    val imageUrl: String?,
    val groupType: GroupType,
    val selectedImageUrl: Uri?,
    val description: String?,
    val descriptionMessage: String?,
    val tags: List<String>?,
    val precautions: String?,
    val recruitInfo: String?,
    val recruit: List<RecruitInfo>?,
    val registeredDate: String?,
    val view: Int?,
    val memberIds: List<String>,
) {
    fun getTotalMaxPersonnel(): Int {
        return recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0
    }

    companion object {
        fun init() = PostUiState(
            title = null,
            imageUrl = null,
            groupType = GroupType.UNKNOWN,
            selectedImageUrl = null,
            description = null,
            descriptionMessage = null,
            tags = emptyList(),
            precautions = null,
            recruitInfo = null,
            recruit = emptyList(),
            registeredDate = null,
            view = null,
            memberIds = emptyList()

        )
    }
}