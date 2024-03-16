package com.seven.colink.domain.entity

import com.seven.colink.domain.model.NotifyType
import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime

data class NotificationEntity(
    val key: String = "NF_" + LocalDateTime.now().toString(),
    val toUserToken: String? = null,
    val type: NotifyType? = null,
    val title: String? = null,
    val name: String? = null,
    val message: String? = null,
    val thumbnail: String? = null,
    val registeredDate: String = LocalDateTime.now().convertLocalDateTime()
)
