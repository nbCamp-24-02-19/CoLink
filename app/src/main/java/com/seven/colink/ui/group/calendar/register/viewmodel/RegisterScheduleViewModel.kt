package com.seven.colink.ui.group.calendar.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ScheduleEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ScheduleRepository
import com.seven.colink.ui.group.calendar.model.ScheduleModel
import com.seven.colink.ui.group.calendar.status.CalendarButtonUiState
import com.seven.colink.ui.group.calendar.status.CalendarEntryType
import com.seven.colink.util.dialog.enum.ColorEnum
import com.seven.colink.util.status.DataResultStatus
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

    private val _uiState = MutableStateFlow(ScheduleModel.init())
    val uiState: StateFlow<ScheduleModel> = _uiState
    private val _complete = MutableSharedFlow<String>()
    val complete = _complete.asSharedFlow()

    private val textItemDataMap: MutableMap<String, String> = mutableMapOf()

    fun setKey(key: String) = viewModelScope.launch {
        groupKey = key
    }

    fun setEntryType(postEntryType: CalendarEntryType) = viewModelScope.launch {
        entryType = postEntryType
        _uiState.value = uiState.value.copy(buttonUiState = setButtonState(null))
    }

    fun setEntity(key: String) = viewModelScope.launch {
        val schedule = scheduleRepository.getScheduleDetail(key).getOrNull()?.setUi()
        _uiState.value = schedule ?: ScheduleModel.init()
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
        _uiState.value = uiState.value.copy(calendarColor = color)
    }

    fun onChangedDateTime(key: String, datetime: String) {
        when (key) {
            "startDate" -> {
                _uiState.value = uiState.value.copy(startDate = datetime)
            }

            "endDate" -> {
                _uiState.value = uiState.value.copy(endDate = datetime)
            }

            else -> Unit
        }
    }

    fun onChangedEditTexts(schedule: String, description: String) {
        textItemDataMap["schedule"] = schedule
        textItemDataMap["description"] = description
    }

    private fun updateTexts() {
        _uiState.value = uiState.value.copy(
            title = textItemDataMap["schedule"] ?: uiState.value.title,
            description = textItemDataMap["description"] ?: uiState.value.description
        )
    }

    fun onClickCompleteButton() {
        when (uiState.value.buttonUiState) {
            CalendarButtonUiState.Create -> onClickCreate()
            CalendarButtonUiState.Update -> _uiState.value = uiState.value.copy(buttonUiState = CalendarButtonUiState.Editing)
            CalendarButtonUiState.Editing -> onClickUpdate()
            else -> Unit
        }
    }

    private fun onClickCreate() = viewModelScope.launch {
        val schedule = uiState.value.convert()
        val result = scheduleRepository.registerSchedule(schedule)
        handleResult(result)
    }

    private fun onClickUpdate() = viewModelScope.launch {
        val schedule = uiState.value.convert()
        val result = scheduleRepository.updateSchedule(schedule.key!!, schedule)
        handleResult(result)
    }

    fun onClickDelete() = viewModelScope.launch {
        if (uiState.value.buttonUiState != CalendarButtonUiState.Editing) return@launch
        val result = scheduleRepository.deleteSchedule(uiState.value.key!!)
        handleResult(result)
    }

    private fun handleResult(result: DataResultStatus) = viewModelScope.launch {
        val emitResult = if (result == DataResultStatus.SUCCESS) "success" else "failed"
        _complete.emit(emitResult)
    }

    private suspend fun ScheduleEntity.setUi(): ScheduleModel {
        val colorEnum = calendarColor ?: ColorEnum.UNKNOWN
        return ScheduleModel(
            key = key,
            authId = authId,
            groupId = groupId,
            startDate = startDate,
            endDate = endDate,
            calendarColor = colorEnum,
            title = title,
            description = description,
            buttonUiState = setButtonState(authId)
        )
    }

    private suspend fun ScheduleModel.convert() = ScheduleEntity(
        key = key ?: ("SE_" + UUID.randomUUID()),
        authId = authId ?: authRepository.getCurrentUser().message,
        groupId = groupId ?: groupKey,
        startDate = startDate,
        endDate = endDate,
        calendarColor = calendarColor,
        title = title,
        description = description
    )

}