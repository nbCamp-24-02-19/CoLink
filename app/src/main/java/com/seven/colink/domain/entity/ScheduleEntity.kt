package com.seven.colink.domain.entity

import com.seven.colink.util.dialog.enum.ColorEnum
import java.time.LocalDateTime
import java.util.UUID

data class ScheduleEntity(
    val key: String = "SE_" + UUID.randomUUID(),
    val authId: String? = "",
    val groupId: String? = "",
    val startDate: String? = "",
    val endDate: String? = "",
    val calendarColor: ColorEnum? = ColorEnum.UNKNOWN,
    val registerDate: String = LocalDateTime.now().toString(),
    val title: String? = "",
    val description: String? = "",
)
