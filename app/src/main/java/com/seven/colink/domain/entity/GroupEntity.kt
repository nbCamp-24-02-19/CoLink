package com.seven.colink.domain.entity

import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.util.UUID

data class GroupEntity (
    val key: String = UUID.randomUUID().toString(),
    val postKey: String = "",
    val authId: String? = "",
    val title: String? = "",
    val imageUrl: String? = "",
    val status: ProjectStatus = ProjectStatus.RECRUIT,
    val groupType: GroupType = GroupType.UNKNOWN,
    val description: String? = "",
    val tags: List<String>? = emptyList(),
    val memberIds: List<String> = emptyList(),
    val registeredTime: String? = "",
    val startDate: String? = "",
    val endDate: String? = "",
)
