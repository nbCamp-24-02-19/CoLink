package com.seven.colink.ui.group.calendar.model

import com.seven.colink.util.dialog.enum.ColorEnum

sealed interface CalendarItem {
    data class ScheduleModel(
        val authId: String?,
        val groupId: String?,
        val startDate: String?,
        val endDate: String?,
        val calendarColor: ColorEnum?,
        val title: String?,
        val description: String?,
    ) : CalendarItem
}