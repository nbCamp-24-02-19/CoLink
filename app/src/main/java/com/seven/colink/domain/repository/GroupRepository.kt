package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.ProjectStatus
import kotlinx.coroutines.flow.Flow

interface GroupRepository {

    suspend fun registerGroup(group: GroupEntity): DataResultStatus
    suspend fun getGroupDetail(key: String): Result<GroupEntity?>
    suspend fun updateGroupSection(key: String, updatedGroup: GroupEntity): DataResultStatus
    suspend fun getGroupByContainUserId(userId: String): Result<List<GroupEntity>>
    suspend fun updateGroupStatus(key: String, status: ProjectStatus, date: Map<String, String>): DataResultStatus
    suspend fun updateGroup(key: String, updatedGroup: GroupEntity): DataResultStatus
    suspend fun updateGroupMemberIds(key: String, updatedGroup: GroupEntity): DataResultStatus
    suspend fun observeGroupState(): Flow<List<GroupEntity>>
}
