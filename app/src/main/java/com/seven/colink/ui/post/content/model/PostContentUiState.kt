package com.seven.colink.ui.post.content.model

data class PostContentUiState(
    val list: List<PostContentItem>
) {
    companion object {
        fun init() = PostContentUiState(
            list = emptyList()
        )
    }
}

sealed interface PostContentButtonUiState {
    data object Writer : PostContentButtonUiState
    data object Supporter : PostContentButtonUiState
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