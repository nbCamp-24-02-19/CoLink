package com.seven.colink.ui.showmore

import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

data class MyPageShowMoreModel(
    val key: String? = null,
    val title: String? = null,
    val ing: ProjectStatus? = null,
    val grouptype: GroupType? = null,
    val time: String? = null,
    val view: Int? = -1,
    val tag: List<String>? = emptyList(),
    val name: String? = null,
    val image: String? = null,
    val description: String? = null,
    val authId: String? = "",
)
