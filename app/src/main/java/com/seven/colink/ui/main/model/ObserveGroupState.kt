package com.seven.colink.ui.main.model

import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

data class ObserveGroupState(
    val groupId: String,
    val state: ProjectStatus,
    val type: GroupType,
)
