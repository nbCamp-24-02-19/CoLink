package com.seven.colink.ui.group.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ScheduleEntity
import com.seven.colink.domain.repository.ScheduleRepository
import com.seven.colink.ui.group.calendar.model.ScheduleItem
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import com.seven.colink.util.Constants.Companion.CALENDAR_TIME_FORMAT
import com.seven.colink.util.dialog.enum.ColorEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<List<ScheduleModel>>(emptyList())
    val uiState: StateFlow<List<ScheduleModel>> get() = _uiState
    private val _filteredByDate = MutableStateFlow(ScheduleItem.init())
    val filteredByDate: StateFlow<ScheduleItem> get() = _filteredByDate
    private val _filteredByMonth = MutableStateFlow<List<ScheduleModel>>(emptyList())
    val filteredByMonth: StateFlow<List<ScheduleModel>> get() = _filteredByMonth

    fun setEntity(key: String, date: LocalDate) = viewModelScope.launch {
        resetFilters()

        val scheduleList = scheduleRepository.getScheduleListByPostId(key).map { it.convert() }
        _uiState.value = scheduleList
        filterScheduleListByDate(date)
        filterDataByMonth(date)
    }

    private fun resetFilters() {
        _filteredByDate.value = ScheduleItem.init()
        _filteredByMonth.value = emptyList()
    }

    fun filterScheduleListByDate(date: LocalDate) = viewModelScope.launch {
        val allScheduleList = uiState.value
        val filteredScheduleList = mutableListOf<ScheduleModel?>()
        allScheduleList.forEach { schedule ->
            val startDate = schedule.startDate?.let {
                LocalDate.parse(
                    it,
                    DateTimeFormatter.ofPattern(CALENDAR_TIME_FORMAT)
                )
            }
            val endDate = schedule.endDate?.let {
                LocalDate.parse(
                    it,
                    DateTimeFormatter.ofPattern(CALENDAR_TIME_FORMAT)
                )
            }

            val datesInRange = getDateRange(startDate, endDate)
            if (datesInRange.contains(date)) {
                filteredScheduleList.add(schedule)
            }
        }

        val sortedList = filteredScheduleList.sortedBy {
            LocalDate.parse(
                it?.startDate,
                DateTimeFormatter.ofPattern(CALENDAR_TIME_FORMAT)
            )
        }

        val finalList = sortedList.ifEmpty {
            listOf(
                ScheduleModel(
                    key = null,
                    authId = null,
                    groupId = null,
                    startDate = null,
                    endDate = null,
                    calendarColor = null,
                    title = null,
                    description = null,
                    buttonUiState = null
                )
            )
        }

        _filteredByDate.value = filteredByDate.value.copy(list = finalList, date = date)
    }

    fun filterDataByMonth(date: LocalDate) {
        val monthData = uiState.value.filter { schedule ->
            val startDate = schedule.startDate?.let {
                LocalDate.parse(
                    it,
                    DateTimeFormatter.ofPattern(CALENDAR_TIME_FORMAT)
                )
            }
            val endDate = schedule.endDate?.let {
                LocalDate.parse(
                    it,
                    DateTimeFormatter.ofPattern(CALENDAR_TIME_FORMAT)
                )
            }
            val isSameMonth = { dateToCheck: LocalDate? ->
                dateToCheck?.month == date.month
            }
            isSameMonth(startDate) || isSameMonth(endDate)
        }

        _filteredByMonth.value = if (monthData.isEmpty()) {
            emptyList()
        } else {
            monthData.map { schedule ->
                ScheduleModel(
                    schedule.key,
                    schedule.authId,
                    schedule.groupId,
                    schedule.startDate,
                    schedule.endDate,
                    schedule.calendarColor,
                    schedule.title,
                    schedule.description,
                    schedule.buttonUiState
                )
            }
        }
    }

    private fun getDateRange(startDate: LocalDate?, endDate: LocalDate?): List<LocalDate> {
        val datesInRange = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (currentDate != null && !currentDate.isAfter(endDate)) {
            datesInRange.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        return datesInRange
    }

    private fun ScheduleEntity.convert() =
        ScheduleModel(
            key = key,
            authId = authId,
            groupId = groupId,
            startDate = startDate,
            endDate = endDate,
            calendarColor = calendarColor ?: ColorEnum.UNKNOWN,
            title = title,
            description = description,
            buttonUiState = null
        )

}