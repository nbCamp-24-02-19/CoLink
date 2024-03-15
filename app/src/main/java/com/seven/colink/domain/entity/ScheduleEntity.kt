package com.seven.colink.domain.entity

import java.time.LocalDateTime
import java.util.UUID

data class ScheduleEntity(
    val key: String = "SE_" + UUID.randomUUID(),
    val authId: String?,
    val groupId: String?,
    val startDate: String?,
    val endDate: String?,
    val calendarColor: String?,
    val title: String?,
    val description: String?,
    val registerDate: String = LocalDateTime.now().toString(),
)
