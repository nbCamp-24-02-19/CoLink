package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.util.status.DataResultStatus

interface GroupRepository {

    suspend fun registerGroup(group: GroupEntity): DataResultStatus
    suspend fun getGroupDetail(key: String): Result<GroupEntity?>
}
