package com.seven.colink.ui.group.calendar.model

import com.seven.colink.ui.group.calendar.status.CalendarButtonUiState
import com.seven.colink.util.convert.getDateByState
import com.seven.colink.util.dialog.enum.ColorEnum
import com.seven.colink.util.status.ScheduleDateType
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
    val buttonUiState: CalendarButtonUiState? = CalendarButtonUiState.Detail
) {
    companion object {
        fun init() = ScheduleModel(
            key = null,
            authId = null,
            groupId = null,
            startDate = getDateByState(ScheduleDateType.CURRENT),
            endDate = getDateByState(ScheduleDateType.AFTER_TWO_HOUR),
            calendarColor = ColorEnum.UNKNOWN,
            title = null,
            description = null,
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
