package com.seven.colink.ui.group.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ScheduleEntity
import com.seven.colink.domain.repository.ScheduleRepository
import com.seven.colink.ui.group.calendar.model.ScheduleModel
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
    private val _filteredSchedules = MutableStateFlow<List<ScheduleModel>>(emptyList())
    val filteredSchedules: StateFlow<List<ScheduleModel>> get() = _filteredSchedules
    fun setEntity(key: String) = viewModelScope.launch {
        val scheduleList = scheduleRepository.getScheduleListByPostId(key).map { it.convert() }
        _uiState.value = scheduleList
        filterScheduleListByDate(LocalDate.now())
    }

    fun filterScheduleListByDate(date: LocalDate) {
        viewModelScope.launch {
            val allScheduleList = uiState.value
            val filteredScheduleList = mutableListOf<ScheduleModel>()

            allScheduleList.forEach { schedule ->
                val startDate = schedule.startDate?.let {
                    LocalDate.parse(
                        it,
                        DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
                    )
                }
                val endDate = schedule.endDate?.let {
                    LocalDate.parse(
                        it,
                        DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
                    )
                }

                val datesInRange = getDateRange(startDate, endDate)

                if (datesInRange.contains(date)) {
                    filteredScheduleList.add(schedule)
                }
            }

            val sortedList = filteredScheduleList.sortedBy { it.startDate }
            _filteredSchedules.value = sortedList
        }
    }

    private fun getDateRange(startDate: LocalDate?, endDate: LocalDate?): List<LocalDate> {
        val datesInRange = mutableListOf<LocalDate>()
        var currentDate = startDate
        while (!currentDate!!.isAfter(endDate)) {
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
            description = description
        )
}