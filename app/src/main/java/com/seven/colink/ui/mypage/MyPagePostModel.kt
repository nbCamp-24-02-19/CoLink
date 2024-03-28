package com.seven.colink.ui.mypage

import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

data class MyPagePostModel(
    val key: String? = null,
    val title: String? = null,
    val ing: ProjectStatus? = null,
    val grouptype: GroupType? = null,
    val time: String? = null,
    val authId: String? = ""
)