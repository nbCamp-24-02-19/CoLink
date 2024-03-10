package com.seven.colink.ui.post.register.post

import com.seven.colink.domain.entity.RecruitInfo

data class PostErrorUiState(
    val message: PostErrorMessage,
) {
    companion object {
        fun init() = PostErrorUiState(
            message = PostErrorMessage.EMPTY,
        )
    }
}

sealed interface PostEvent {
    data class DialogEvent(
        val recruit: List<RecruitInfo>?,
    ) : PostEvent
}
