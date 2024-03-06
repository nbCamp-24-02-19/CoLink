package com.seven.colink.domain.entity

import android.os.Parcelable
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.time.LocalDateTime
import java.util.UUID

data class PostEntity(
    val key: String = "POST_" + UUID.randomUUID().toString(),
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


data class RecruitEntity (
    val key: String,
    val postId: String?,
    val groupId: String?,
    val type: String? = "",
    val maxPersonnel: Int? = -1, // 모집 최대 인원 수
)

data class ApplicationInfoEntity(
    val key: String,
    val recruitId: String?,
    val userId: String? = "", // 지원한 사용자 아이디
    val applicationStatus: ApplicationStatus? = ApplicationStatus.PENDING, // 지원 상태
    val applicationDate: String? = LocalDateTime.now().convertLocalDateTime(), // 지원한 날짜
)