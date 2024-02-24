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
    CHAT(
        title = "chats"
    ),
    PRODUCT(
        title = "products"
    )
}