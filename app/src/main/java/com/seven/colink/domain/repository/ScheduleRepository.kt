package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.ScheduleEntity
import com.seven.colink.util.status.DataResultStatus

interface ScheduleRepository {

    suspend fun registerSchedule(schedule: ScheduleEntity): DataResultStatus
    suspend fun getScheduleByPostId(postId: String): List<ScheduleEntity>
}
