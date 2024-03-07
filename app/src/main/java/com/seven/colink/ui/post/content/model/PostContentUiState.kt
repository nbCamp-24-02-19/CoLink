package com.seven.colink.ui.post.content.model


sealed interface ContentOwnerButtonUiState {
    data object Owner : ContentOwnerButtonUiState
    data object User : ContentOwnerButtonUiState
}

data class DialogUiState(
    val title: String?,
    val message: String?,
    val recruitItem: PostContentItem.RecruitItem?
) {
    companion object {
        fun init() = DialogUiState(
            title = null,
            message = null,
            recruitItem = null
        )
    }
}