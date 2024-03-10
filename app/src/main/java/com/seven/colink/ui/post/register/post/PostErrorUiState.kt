package com.seven.colink.ui.post.register.post

import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.ui.group.content.GroupErrorMessage

data class PostErrorUiState(
    val message: PostErrorMessage,
    val tag: PostErrorMessage
) {
    companion object {
        fun init() = PostErrorUiState(
            message = PostErrorMessage.EMPTY,
            tag = PostErrorMessage.PASS
        )
    }
}

sealed interface PostEvent {
    data class DialogEvent(
        val recruit: List<RecruitInfo>?,
    ) : PostEvent
}
