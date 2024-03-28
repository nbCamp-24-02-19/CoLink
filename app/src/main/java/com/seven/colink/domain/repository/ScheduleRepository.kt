package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.ScheduleEntity
import com.seven.colink.util.status.DataResultStatus

interface ScheduleRepository {

    suspend fun registerSchedule(schedule: ScheduleEntity): DataResultStatus
    suspend fun getScheduleListByPostId(postId: String): List<ScheduleEntity>
    suspend fun getScheduleDetail(key: String): Result<ScheduleEntity?>
    suspend fun deleteSchedule(key: String): DataResultStatus
    suspend fun updateSchedule(key: String, updatedSchedule: ScheduleEntity): DataResultStatus
}
