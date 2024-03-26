package com.seven.colink.ui.group.calendar.model

import com.seven.colink.ui.group.calendar.status.CalendarButtonUiState
import com.seven.colink.util.dialog.enum.ColorEnum
import java.time.LocalDate

data class ScheduleModel(
    val key: String?,
    val authId: String?,
    val groupId: String?,
    val startDate: String?,
    val endDate: String?,
    val calendarColor: ColorEnum?,
    val title: String?,
    val description: String?,
    val buttonUiState: CalendarButtonUiState?
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
            buttonUiState = CalendarButtonUiState.Create
        )
    }
}

data class ScheduleItem(
    val list: List<ScheduleModel?>,
    val date: LocalDate?
) {
    companion object {
        fun init() = ScheduleItem(
            list = emptyList(),
            date = LocalDate.now()
        )
    }
}
