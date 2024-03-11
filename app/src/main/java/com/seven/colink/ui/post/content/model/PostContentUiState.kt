package com.seven.colink.ui.post.content.model

import com.seven.colink.util.status.GroupType


sealed interface ContentButtonUiState {
    data object Manager : ContentButtonUiState
    data object User : ContentButtonUiState
    data object Unknown : ContentButtonUiState
}

data class DialogUiState(
    val title: String?,
    val message: String?,
    val groupType: GroupType?,
    val recruitItem: PostContentItem.RecruitItem?
) {
    companion object {
        fun init() = DialogUiState(
            title = null,
            message = null,
            groupType = null,
            recruitItem = null
        )
    }
}