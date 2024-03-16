package com.seven.colink.domain.entity

import com.seven.colink.domain.model.NotifyType
import java.time.LocalDateTime

data class NotificationEntity(
    val key: String = "NF_" + LocalDateTime.now().toString(),
    val type: NotifyType,
    val title: String,
    val registeredDate: String = LocalDateTime.now().toString()
)
