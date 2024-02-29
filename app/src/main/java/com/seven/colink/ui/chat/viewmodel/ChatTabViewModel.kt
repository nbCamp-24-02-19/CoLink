package com.seven.colink.ui.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.ui.chat.ChatTabFragment.Companion.CHAT_TYPE
import com.seven.colink.ui.chat.type.ChatTabType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatTabViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val handle: SavedStateHandle,
): ViewModel() {

    private val _chatType:MutableStateFlow<ChatTabType> = MutableStateFlow(ChatTabType.GENERAL)
    val chatType: StateFlow<ChatTabType> = _chatType

    init {
        _chatType.value = handle.get<ChatTabType>(CHAT_TYPE)?: ChatTabType.GENERAL
    }

}