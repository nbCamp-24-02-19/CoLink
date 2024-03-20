package com.seven.colink.ui.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.chat.ChatTabFragment.Companion.CHAT_TYPE
import com.seven.colink.ui.chat.model.ChatListItem
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.util.convert.convertTime
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatTabViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    handle: SavedStateHandle,
): ViewModel() {

    private val _chatType = MutableStateFlow(ChatTabType.GENERAL)
    val chatType: StateFlow<ChatTabType> = _chatType

    private val _chatList = MutableStateFlow<UiState<List<ChatListItem>>>(UiState.Loading)
    val chatList: StateFlow<UiState<List<ChatListItem>>> = _chatList

    init {
        _chatType.value = handle.get<ChatTabType>(CHAT_TYPE)?: ChatTabType.GENERAL
    }

    suspend fun setChat() {
        viewModelScope.launch {
            _chatList.value = UiState.Loading
            try {
                val result = authRepository.getCurrentUser()
                if (result == DataResultStatus.SUCCESS) {
                    val list = userRepository.getUserDetails(result.message)
                        .getOrNull()?.participantsChatRoomIds?.map {
                        async {
                            chatRepository.getChatRoom(it)?.convert(result.message, chatType.value)
                        }
                    } ?: return@launch
                    _chatList.value = UiState.Success(list.awaitAll().filterNotNull().sortedByDescending { it.recentTime })
                }
            } catch (e: Exception) {
                _chatList.value = UiState.Error(e)
            }
        }
    }

    private suspend fun ChatRoomEntity.convert(uid: String, tabType: ChatTabType) = withContext(Dispatchers.IO) {
        if (type != tabType) return@withContext null
        var opponent: UserEntity? = null
        if (type == ChatTabType.GENERAL) opponent = userRepository.getUserDetails(participantsUid.keys.first { it != uid }).getOrNull()
        val title = opponent?.name?: title
        val thumbnail = opponent?.photoUrl?: thumbnail
        val messageDeferred =
            async {
                chatRepository.getChatRoomMessage(key)
            }
        val message = messageDeferred.await()
        ChatListItem(
            key = key,
            title = title.toString(),
            message = message?.lastOrNull()?.text?: return@withContext null,
            thumbnail = thumbnail,
            recentTime = message.last().registerDate.convertTime(),
            unreadCount = message.count { entity -> entity.viewUsers.none { it == uid } }
        )
    }

}