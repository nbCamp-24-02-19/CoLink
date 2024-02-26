package com.seven.colink.domain.entity

import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

data class ProductEntity (
    val key: String = "PRD_" + UUID.randomUUID().toString(),
    val projectId: String = "",
    val authId: String? = "",
    val title: String? = "",
    val imageUrl: String? = "",
    val description: String? = "",
    val tags: List<String>? = emptyList(),
    val memberIds: List<String> = emptyList(),
    val registeredDate: String? = LocalDateTime.now().convertLocalDateTime(),
    val referenceUrl: String? = "http:",
)
