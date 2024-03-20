package com.seven.colink.ui.post.content.model

import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

data class Comment (
    val key: String = "CMT_" + UUID.randomUUID().toString(),
    val authId: String = "",
    val postId: String = "",
    val description: String = "",
    val registeredDate: String = LocalDateTime.now().convertLocalDateTime(),
)