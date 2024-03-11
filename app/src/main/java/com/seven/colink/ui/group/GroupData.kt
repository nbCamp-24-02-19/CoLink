package com.seven.colink.ui.group

import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.GroupType
import java.time.LocalDateTime
import java.util.UUID

sealed interface GroupData {
    data class GroupTitle(
        val title: String
    ) : GroupData

    data class GroupList(
        val key: String?,
        val groupType: GroupType?,
        val thumbnail: String?,
        val projectName: String?,
        val days: String?,
        val description: String?,
        val tags: List<String>? = emptyList(),
        val memberIds: List<String> = emptyList()
    ) : GroupData

    data class GroupAdd(
        val addGroupImage: Int,
        val addGroupText: String,
        val appliedGroup: String
    ) : GroupData

    data class GroupWant(
        val key: String?,
        val groupType: GroupType?,
        val title: String?,
        val description: String?,
        val kind: String?,
        val img: String?
    ) : GroupData

    data class GroupEmpty(
        var img: Int?,
        var text: String?
    ) : GroupData
}