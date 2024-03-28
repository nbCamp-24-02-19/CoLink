package com.seven.colink.data.remote.dto

import com.seven.colink.util.status.GroupType

data class NotificationDTO(
    val to: String?,
    val data: NotificationData?
){
    data class NotificationData (
        val key: String?,
        val title: String?,
        val type: String?,
        val name: String?,
        val img: String?,
        val message: String?,
        val registeredDate: String?,
        val groupType: String?,
    )
}
