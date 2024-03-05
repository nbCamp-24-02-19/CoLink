package com.seven.colink.ui.search

import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.time.LocalDateTime
import java.util.UUID

data class SearchModel(
    val key: String = "POST_" + UUID.randomUUID().toString(),
    val authId: String? = "",
    val title: String? = "",
    val status: ProjectStatus? = ProjectStatus.RECRUIT,
    val groupType: GroupType?,
    val description: String? = "",
    val tags: List<String>? = emptyList(),
    val registeredDate: String?,
    val views: Int? = -1
)
