package com.seven.colink.ui.search

import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.time.LocalDateTime
import java.util.UUID

data class SearchModel(
    val key: String = "POST_" + UUID.randomUUID().toString(),
    val thumbnail : String? = "https://firebasestorage.googleapis.com/v0/b/colink-a7c3a.appspot.com/o/img%2F2ad7abe5-7945-47ba-b5ca-611278836783.jpg?alt=media&token=a1445ac5-b90f-4af3-b2de-9e07052bb497",
    val authId: String? = "",
    val title: String? = "",
    val status: ProjectStatus? = ProjectStatus.RECRUIT,
    val groupType: GroupType?,
    val description: String? = "",
    val tags: List<String>? = emptyList(),
    val registeredDate: String?,
    val views: Int? = 0,
    val likes: Int? = 0
)
