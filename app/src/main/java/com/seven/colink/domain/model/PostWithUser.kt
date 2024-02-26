package com.seven.colink.domain.model

import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity

data class PostWithUser(
    val post: PostEntity?,
    val users: List<UserEntity?>?,
)
