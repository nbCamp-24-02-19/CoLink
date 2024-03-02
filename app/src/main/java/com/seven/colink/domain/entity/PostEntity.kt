package com.seven.colink.domain.entity

import android.os.Parcelable
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class PostEntity(
    val key: String = "POST_" + UUID.randomUUID().toString(),
    val authId: String? = "",
    val title: String? = "",
    val imageUrl: String? = "",
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
) : Parcelable
