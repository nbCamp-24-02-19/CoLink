package com.seven.colink.domain.entity

import java.util.UUID

data class TagEntity(
    val key: String = UUID.randomUUID().toString(),
    val name: String,
)