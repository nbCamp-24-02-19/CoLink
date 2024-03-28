package com.seven.colink.ui.group.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.ui.group.calendar.status.CalendarEntryType
import com.seven.colink.util.Constants
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GroupSharedViewModel  @Inject constructor(
    handle: SavedStateHandle,
): ViewModel() {

    private val _entryType = MutableStateFlow(handle.get<PostEntryType>(Constants.EXTRA_ENTRY_TYPE))
    val entryType: StateFlow<PostEntryType?> = _entryType

    private val _scheduleEntryType = MutableStateFlow<CalendarEntryType?>(null)
    val scheduleEntryType: StateFlow<CalendarEntryType?> = _scheduleEntryType

    private val _key = MutableStateFlow(handle.get<String>(Constants.EXTRA_ENTITY_KEY))
    val key: StateFlow<String?> = _key

    private val _scheduleKey = MutableStateFlow<String?>(null)
    val scheduleKey: StateFlow<String?> = _scheduleKey

    fun setKey(newKey: String) {
        _key.value = newKey
    }

    fun setScheduleKey(newKey: String?) {
        _scheduleKey.value = newKey
    }

    fun setEntryType(entryType: PostEntryType) {
        _entryType.value = entryType
    }

    fun setScheduleEntryType(entryType: CalendarEntryType) {
        _scheduleEntryType.value = entryType
    }

}