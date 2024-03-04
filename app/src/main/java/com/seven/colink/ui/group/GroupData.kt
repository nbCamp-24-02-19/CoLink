package com.seven.colink.ui.group

import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.util.status.GroupType
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
        val days: Int?,
        val description: String?,
        val tags: List<String>? = emptyList()
    ) : GroupData

    data class GroupItem(
        val group: GroupEntity?
    ) : GroupData

    data class GroupAdd(
        val addGroupImage: Int,
        val addGroupText: String,
        val appliedGroup: String
    ) : GroupData

    data class GroupWant(
        val groupType: GroupType?,
        val title: String?,
        val description: String?,
        val kind: String?,
        val lv: String?,
        val img: Int?
    ) : GroupData
}