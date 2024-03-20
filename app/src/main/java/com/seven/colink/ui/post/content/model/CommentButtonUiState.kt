package com.seven.colink.ui.post.content.model

sealed interface CommentButtonUiState{
    data object Manager : CommentButtonUiState
    data object User : CommentButtonUiState
    data object Unknown : CommentButtonUiState
}
