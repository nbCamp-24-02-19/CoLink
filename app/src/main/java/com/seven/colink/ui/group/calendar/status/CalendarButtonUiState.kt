package com.seven.colink.ui.group.calendar.status

sealed interface CalendarButtonUiState {
    data object Create : CalendarButtonUiState
    data object Update : CalendarButtonUiState
    data object Detail : CalendarButtonUiState
    data object Editing : CalendarButtonUiState
}

enum class CalendarEntryType {
    CREATE,
    UPDATE,
    DETAIL
    ;
}