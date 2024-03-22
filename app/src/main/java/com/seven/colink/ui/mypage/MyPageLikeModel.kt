package com.seven.colink.ui.mypage

import com.seven.colink.util.status.ProjectStatus

data class MyPageLikeModel(
    val key: String?,
    val title: String?,
    val status: ProjectStatus?,
    val time: String?
)