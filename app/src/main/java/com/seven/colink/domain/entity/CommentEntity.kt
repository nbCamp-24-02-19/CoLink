package com.seven.colink.domain.entity

import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

data class CommentEntity(
    val key: String = "CMT_" + UUID.randomUUID().toString(),
    val authId: String = "",
    val postId: String = "",
    val description: String = "",
    val registeredDate: String = LocalDateTime.now().convertLocalDateTime(),
)
