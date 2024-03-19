package com.seven.colink.ui.group.calendar.model

import com.seven.colink.ui.group.calendar.status.CalendarButtonUiState
import com.seven.colink.util.dialog.enum.ColorEnum

data class ScheduleModel(
    val key: String?,
    val authId: String?,
    val groupId: String?,
    val startDate: String?,
    val endDate: String?,
    val calendarColor: ColorEnum?,
    val title: String?,
    val description: String?,
    val buttonUiState: CalendarButtonUiState? = CalendarButtonUiState.Detail
) {
    companion object {
        fun init() = ScheduleModel(
            key = null,
            authId = null,
            groupId = null,
            startDate = null,
            endDate = null,
            calendarColor = ColorEnum.UNKNOWN,
            title = null,
            description = null,
        )
    }
}