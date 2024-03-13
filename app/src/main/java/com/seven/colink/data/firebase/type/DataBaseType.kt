package com.seven.colink.data.firebase.type

enum class DataBaseType(
    val title: String
) {
    USER(
        title = "users"
    ),
    GROUP(
        title = "groups"
    ),
    POST(
        title = "posts"
    ),
    COMMENT(
        title = "comments"
    ),
    CHATROOM(
        title = "chatRooms"
    ),
    MESSAGE(
        title = "messages"
    )
    ,
    PRODUCT(
        title = "products"
    ),
    RECRUIT(
        title = "recruits"
    ),
    APPINFO(
        title = "app_info"
    ),
    SCHEDULE(
        title = "schedule"
    ),
}