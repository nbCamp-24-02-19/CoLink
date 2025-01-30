package com.seven.colink.util.status

enum class UserStatus(
    val info: String? = "member"
) {
    MEMBER(
        info = "member"
    ),
    LEAVER(
        info = "leaver"
    )
    ;
}