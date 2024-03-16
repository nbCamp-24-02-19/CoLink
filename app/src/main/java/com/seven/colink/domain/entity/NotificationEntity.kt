package com.seven.colink.domain.entity

import com.seven.colink.domain.model.NotifyType
import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime

data class NotificationEntity(
    val key: String = "NF_" + LocalDateTime.now().toString(),
    val toUserToken: String?,
    val type: NotifyType?,
    val title: String?,
    val name: String?,
    val message: String?,
    val thumbnail: String?,
    val registeredDate: String = LocalDateTime.now().convertLocalDateTime()
)
