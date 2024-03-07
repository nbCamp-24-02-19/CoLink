package com.seven.colink.ui.post.register.post.model

import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.time.LocalDateTime
import java.util.UUID

data class Post (
    val key: String = UUID.randomUUID().toString(),
    val authId: String? = "",
    val title: String? = "",
    val imageUrl: String? = "https://firebasestorage.googleapis.com/v0/b/colink-a7c3a.appspot.com/o/img%2F0c662c2c-0a51-475f-8b1e-d54007b9abee.jpg?alt=media&token=e9b5714e-bbae-4731-bf24-b674c1a39c99",
    val status: ProjectStatus = ProjectStatus.RECRUIT,
    val groupType: GroupType? = GroupType.UNKNOWN,
    val description: String? = "",
    val tags: List<String>? = emptyList(),
    val precautions: String? = "",
    val recruitInfo: String? = "",
    val recruit: List<RecruitInfo>? = emptyList(),
    val registeredDate: String? = LocalDateTime.now().convertLocalDateTime(),
    val editDate: String? = null,
    val views: Int? = 0,
    val startDate: String? = "",
    val endDate: String? = "",
    val memberIds: List<String> = emptyList(),
)