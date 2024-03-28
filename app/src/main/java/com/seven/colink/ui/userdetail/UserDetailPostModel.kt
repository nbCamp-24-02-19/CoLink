package com.seven.colink.ui.userdetail

import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

data class UserDetailPostModel(
    val key: String? = null,
    val title: String? = null,
    val ing: ProjectStatus? = null,
    val grouptype: GroupType? = null,
    val time: String? = null
)
