package com.seven.colink.ui.post.register.post


data class PostErrorUiState(
    val message: PostErrorMessage,
    val recruit: PostErrorMessage,
    val tag: PostErrorMessage
) {
    companion object {
        fun init() = PostErrorUiState(
            message = PostErrorMessage.EMPTY,
            recruit = PostErrorMessage.EMPTY,
            tag = PostErrorMessage.PASS
        )
    }
}
