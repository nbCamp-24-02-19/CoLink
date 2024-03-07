package com.seven.colink.ui.post.register.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
@HiltViewModel
class PostSharedViewModel @Inject constructor(
    handle: SavedStateHandle,
): ViewModel() {

    private val _entryType = MutableStateFlow(handle.get<PostEntryType>(Constants.EXTRA_ENTRY_TYPE))
    val entryType: StateFlow<PostEntryType?> = _entryType

    private val _groupType = MutableStateFlow(handle.get<GroupType>(Constants.EXTRA_GROUP_TYPE))
    val groupType: StateFlow<GroupType?> = _groupType

    private val _key = MutableStateFlow(handle.get<String>(Constants.EXTRA_ENTITY_KEY))
    val key: StateFlow<String?> = _key

    fun setKey(newKey: String) {
        _key.value = newKey
    }

    fun setEntryType(updateEntryType: PostEntryType) {
        _entryType.value = updateEntryType
    }
}