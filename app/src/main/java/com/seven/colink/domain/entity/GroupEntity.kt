package com.seven.colink.domain.entity

import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.time.LocalDateTime
import java.util.UUID

data class GroupEntity (
    val key: String = "GROUP_" + UUID.randomUUID().toString(),
    val postKey: String = "",
    val authId: String? = "",
    val title: String? = "",
    val imageUrl: String? = "",
    val status: ProjectStatus = ProjectStatus.RECRUIT,
    val groupType: GroupType = GroupType.UNKNOWN,
    val description: String? = "",
    val tags: List<String>? = emptyList(),
    val memberIds: List<String> = emptyList(),
    val registeredDate: String? = LocalDateTime.now().convertLocalDateTime(),
    val startDate: String? = null,
    val endDate: String? = "",
)
