package com.seven.colink.ui.post.content.model


sealed interface ContentButtonUiState {
    data object Manager : ContentButtonUiState
    data object User : ContentButtonUiState
    data object Unknown : ContentButtonUiState

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