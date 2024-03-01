package com.seven.colink.ui.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.chat.ChatTabFragment.Companion.CHAT_TYPE
import com.seven.colink.ui.chat.model.ChatListItem
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.util.status.DataResultStatus
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
    private val chatType: StateFlow<ChatTabType> = _chatType

    private val _chatList = MutableStateFlow<List<ChatListItem>>(emptyList())
    val chatList: StateFlow<List<ChatListItem>> = _chatList

    init {
        _chatType.value = handle.get<ChatTabType>(CHAT_TYPE)?: ChatTabType.GENERAL
        setChat()
    }

    private fun setChat() = viewModelScope.launch {
        val result = authRepository.getCurrentUser()
        if (result == DataResultStatus.SUCCESS) {
            val chatRooms = chatRepository.getChatRoomList(result.message, chatType.value).getOrNull() ?: emptyList()
            val deferredList = chatRooms.map { chatRoom ->
                async { chatRoom.convert(result.message) }
            }

            val convertedChatRooms = deferredList.awaitAll().filterNotNull()
            _chatList.value = convertedChatRooms
        }
    }

    private suspend fun ChatRoomEntity.convert(uid: String) = withContext(Dispatchers.IO) {
        val title =
            if (type != ChatTabType.GENERAL){
                title
            }else {
                participantsUid.singleOrNull { it != uid }
            }
        val thumbnailDeferred =
            async {
                if (type == ChatTabType.GENERAL) {
                    userRepository.getUserDetails(participantsUid.single { it != uid })
                        .getOrNull()?.photoUrl
                } else {
                    thumbnail
                }
            }
        val messageDeferred =
            async {
                chatRepository.getChatRoomMessage(key)
            }

        val thumbnail = thumbnailDeferred.await()
        val message = messageDeferred.await()
        ChatListItem(
            key = key,
            title = title.toString(),
            message = message.first().text?: return@withContext null,
            thumbnail = thumbnail,
            recentTime = message.first().registerDate,
            unreadCount = message.count { entity -> entity.viewUsers.none { it == uid } }
        )
    }

}