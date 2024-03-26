package com.seven.colink.ui.group.calendar.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ScheduleEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ScheduleRepository
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import com.seven.colink.ui.group.calendar.status.CalendarButtonUiState
import com.seven.colink.ui.group.calendar.status.CalendarEntryType
import com.seven.colink.util.convert.getDateByState
import com.seven.colink.util.dialog.enum.ColorEnum
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.ScheduleDateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private lateinit var groupKey: String
    private lateinit var entryType: CalendarEntryType

    private val _uiState = MutableStateFlow<List<ScheduleModel>?>(emptyList())
    val uiState: StateFlow<List<ScheduleModel>?> = _uiState

    private val _complete = MutableSharedFlow<String>()
    val complete = _complete.asSharedFlow()

    private val textItemDataMap: MutableMap<String, String> = mutableMapOf()

    fun setKey(key: String) {
        groupKey = key
    }

    fun setEntryType(postEntryType: CalendarEntryType) {
        entryType = postEntryType
        if (entryType == CalendarEntryType.CREATE) {
            createEntity()
        }
    }

    fun setEntity(key: String) = viewModelScope.launch {
        _uiState.value =
            scheduleRepository.getScheduleDetail(key).getOrNull()?.setUi() ?: emptyList()
    }

    private fun createEntity() = viewModelScope.launch {
        _uiState.value = ScheduleEntity(
            startDate = getDateByState(ScheduleDateType.CURRENT),
            endDate = getDateByState(ScheduleDateType.AFTER_TWO_HOUR),
            calendarColor = ColorEnum.UNKNOWN,
        ).setUi()
    }

    private suspend fun setButtonState(authId: String?): CalendarButtonUiState {
        val currentUser = authRepository.getCurrentUser().message
        return when (entryType) {
            CalendarEntryType.CREATE -> CalendarButtonUiState.Create
            CalendarEntryType.DETAIL -> {
                if (currentUser == authId) CalendarButtonUiState.Update
                else CalendarButtonUiState.Detail
            }

            CalendarEntryType.UPDATE -> CalendarButtonUiState.Editing
        }
    }

    fun onChangedColor(color: ColorEnum) {
        updateTexts()
        _uiState.value = uiState.value?.map {
            it.copy(calendarColor = color)
        }
    }

    fun onChangedDateTime(key: String, datetime: String) {
        updateTexts()
        _uiState.value = uiState.value?.map {
            when (key) {
                "startDate" -> it.copy(startDate = datetime)
                "endDate" -> it.copy(endDate = datetime)
                else -> it
            }
        }
    }

    fun onChangedEditTexts(schedule: String, description: String) {
        textItemDataMap["schedule"] = schedule
        textItemDataMap["description"] = description
    }

    private fun updateTexts() {
        _uiState.value = uiState.value?.map {
            it.copy(
                title = textItemDataMap["schedule"] ?: it.title,
                description = textItemDataMap["description"] ?: it.description
            )
        }
    }

    fun onClickCompleteButton() {
        when (uiState.value?.firstOrNull()?.buttonUiState) {
            CalendarButtonUiState.Create -> onClickCreate()
            CalendarButtonUiState.Update -> {
                _uiState.value = uiState.value?.map {
                    it.copy(buttonUiState = CalendarButtonUiState.Editing)
                }
            }

            CalendarButtonUiState.Editing -> onClickUpdate()
            else -> Unit
        }
    }

    private fun onClickCreate() {
        viewModelScope.launch {
            val schedule = uiState.value?.firstOrNull()?.convert()
            if (schedule != null) {
                val result = scheduleRepository.registerSchedule(schedule)
                handleResult(result)
            }
        }
    }

    private fun onClickUpdate() {
        viewModelScope.launch {
            val schedule = uiState.value?.firstOrNull()?.convert()
            if (schedule != null) {
                val result = scheduleRepository.updateSchedule(schedule.key, schedule)
                handleResult(result)
            }
        }
    }

    fun onClickDelete() {
        viewModelScope.launch {
            if (uiState.value?.firstOrNull()?.buttonUiState != CalendarButtonUiState.Editing) return@launch
            val result = scheduleRepository.deleteSchedule(uiState.value?.firstOrNull()?.key ?: "")
            handleResult(result)
        }
    }

    private fun handleResult(result: DataResultStatus) {
        viewModelScope.launch {
            val emitResult = if (result == DataResultStatus.SUCCESS) "success" else "failed"
            _complete.emit(emitResult)
        }
    }

    private suspend fun ScheduleEntity.setUi(): List<ScheduleModel> {
        val authId = authRepository.getCurrentUser().message
        return listOf(
            ScheduleModel(
                key = key,
                authId = authId,
                groupId = if (groupId.isNullOrBlank()) groupKey else groupId,
                startDate = startDate,
                endDate = endDate,
                calendarColor = calendarColor ?: ColorEnum.UNKNOWN,
                title = title,
                description = description,
                buttonUiState = setButtonState(authId)
            )
        )
    }


    private suspend fun ScheduleModel.convert() = ScheduleEntity(
        key = key ?: ("SE_" + UUID.randomUUID()),
        authId = authId ?: authRepository.getCurrentUser().message,
        groupId = groupId,
        startDate = startDate ?: getDateByState(ScheduleDateType.CURRENT),
        endDate = endDate ?: getDateByState(ScheduleDateType.AFTER_TWO_HOUR),
        calendarColor = calendarColor,
        title = title?.ifBlank { "제목 없음" },
        description = description
    )
}
