package com.seven.colink.ui.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.chat.ChatRoomActivity.Companion.CHAT_ID
import com.seven.colink.ui.chat.model.ChatRoomItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val handle: SavedStateHandle,
):ViewModel() {
    private val _messageList = MutableStateFlow<List<ChatRoomItem>>(emptyList())
    val messageList: StateFlow<List<ChatRoomItem>> = _messageList

    private var users = 0

    init {
        viewModelScope.launch {
            _messageList.value = chatRepository.getChatRoom(handle[CHAT_ID]!!)!!.run {
                users = participantsUid.size
                chatRepository.getChatRoomMessage(key).map {
                    withContext(Dispatchers.IO) {
                        async { it.convert(users) }
                    }.await()
                }
            }
        }
    }

    fun observeMessage() {
        viewModelScope.launch {
            chatRepository.observeMessages(handle[CHAT_ID]!!) {
                it.map {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            async { it.convert(users) }
                        }.await()
                    }
                }
            }
        }
    }


    private suspend fun MessageEntity.convert(users: Int)
    = if (authId == authRepository.getCurrentUser().message) convertMyMessage(users)
    else convertOtherMessage(users, authId?: "")
    private fun MessageEntity.convertMyMessage(users: Int) = ChatRoomItem.MyMessage(
        key = key,
        text = text.toString(),
        time = registerDate,
        viewCount = (users - viewUsers.size).toString()
    )

    private suspend fun MessageEntity.convertOtherMessage(users: Int, authId: String) = userRepository.getUserDetails(authId).let{
        ChatRoomItem.OtherMessage(
            key = key,
            text = text.toString(),
            time = registerDate,
            viewCount = (users - viewUsers.size).toString(),
            profileUrl = it.getOrNull()?.photoUrl,
            name = it.getOrNull()?.name.toString(),
        )
    }
}
