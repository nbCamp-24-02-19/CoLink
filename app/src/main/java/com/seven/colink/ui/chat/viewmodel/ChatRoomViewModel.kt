package com.seven.colink.ui.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.chat.ChatRoomActivity.Companion.CHAT_ID
import com.seven.colink.ui.chat.model.ChatRoomItem
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
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val handle: SavedStateHandle,
) : ViewModel() {
    private val _messageList = MutableStateFlow<List<ChatRoomItem>?>(emptyList())

    val messageList: StateFlow<List<ChatRoomItem>?> = _messageList
    private val chatRoomKey get() = handle.get<String>(CHAT_ID)

    private val _chatRoom = MutableStateFlow(ChatRoomEntity())
    val chatRoom: StateFlow<ChatRoomEntity> = _chatRoom

    private lateinit var uid: String

    init {
        viewModelScope.launch {
            val chatRoomDeferred = async { chatRepository.getChatRoom(chatRoomKey!!)!! }
            val uidDeferred = async { authRepository.getCurrentUser().message }

            _chatRoom.value = chatRoomDeferred.await()
            uid = uidDeferred.await()
        }
    }

    suspend fun setMessages(room: ChatRoomEntity) {
        _messageList.value = chatRoom.run {
            chatRepository.getChatRoomMessage(room.key)?.map {
                withContext(Dispatchers.IO) {
                    async { it.convert(room.participantsUid.count()) }
                }.await()
            }
        }
    }

    suspend fun observeMessage(room: ChatRoomEntity) {
        chatRepository.observeMessages(room) { messages ->
            viewModelScope.launch(Dispatchers.IO) {
                _messageList.value = messages.map { message ->
                    async(Dispatchers.IO) { message.convert(room.participantsUid.size) }
                }.awaitAll()
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                MessageEntity(
                    chatRoomId = chatRoom.value.key,
                    text = text,
                    authId = uid,
                    viewUsers = listOf(uid)
                )
            )
        }
    }

    private suspend fun MessageEntity.convert(users: Int) =
        if (authId == uid) convertMyMessage(users)
        else convertOtherMessage(users, authId ?: "")

    private fun MessageEntity.convertMyMessage(users: Int) = ChatRoomItem.MyMessage(
        key = key,
        text = text.toString(),
        time = registerDate,
        viewCount = (users - viewUsers.size).toString()
    )

    private suspend fun MessageEntity.convertOtherMessage(users: Int, authId: String) =
        userRepository.getUserDetails(authId).let {
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
