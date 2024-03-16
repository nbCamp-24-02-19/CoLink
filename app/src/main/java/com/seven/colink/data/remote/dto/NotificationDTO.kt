package com.seven.colink.data.remote.dto

data class NotificationDTO(
    val to: String?,
    val data: NotificationData?
){
    data class NotificationData (
        val title: String?,
        val type: String?,
        val name: String?,
        val message: String?,
        val registeredDate: String?,
    )
}
