package com.seven.colink.domain.entity

import java.util.UUID

data class ProductEntity (
    val key: String = UUID.randomUUID().toString(),
    val projectId: String = "",
    val authId: String? = "",
    val title: String? = "",
    val imageUrl: String? = "",
    val description: String? = "",
    val tags: List<String>? = emptyList(),
    val memberIds: List<String> = emptyList(),
    val registeredTime: String? = "",
    val referenceUrl: String? = "http:",
)
