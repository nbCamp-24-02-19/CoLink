package com.seven.colink.ui.chat.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.SendNotificationUseCase
import com.seven.colink.ui.chat.ChatRoomActivity.Companion.CHAT_ID
import com.seven.colink.ui.chat.model.ChatRoomItem
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.util.convert.convertTime
import com.seven.colink.util.convert.containsUrl
import com.seven.colink.util.convert.extractUrl
import com.seven.colink.util.convert.fetchUrlMetaData
import com.seven.colink.util.model.UrlMetaData
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val imageRepository: ImageRepository,
    private val handle: SavedStateHandle,
) : ViewModel() {
    private val _messageList = MutableStateFlow<UiState<List<ChatRoomItem>?>>(UiState.Loading)

    val messageList: StateFlow<UiState<List<ChatRoomItem>?>> = _messageList
    private val chatRoomKey get() = handle.get<String>(CHAT_ID)

    private val _chatRoom = MutableStateFlow(ChatRoomEntity())
    val chatRoom: StateFlow<ChatRoomEntity> = _chatRoom

    private val _selectedImage = MutableStateFlow<Uri>(Uri.EMPTY)
    val selectedImage = _selectedImage.asStateFlow()

    private var _uid: String? = null
    private val uid get() = _uid!!

    init {
        viewModelScope.launch {
            val chatRoomDeferred = async { chatRepository.getChatRoom(chatRoomKey!!)!! }
            val uidDeferred = async { authRepository.getCurrentUser().message }

            _chatRoom.value = chatRoomDeferred.await()
            _uid = uidDeferred.await()

            if (chatRoom.value.type == ChatTabType.GENERAL) {
                _chatRoom.value = _chatRoom.value.copy(
                    title = userRepository.getUserDetails(uid).getOrNull()?.name
                )
            }

            setMessages(chatRoom.value)
        }
    }

    private suspend fun setMessages(room: ChatRoomEntity) {
        _messageList.value =
            try {
                UiState.Success(chatRoom.run {
                    chatRepository.getChatRoomMessage(room.key)?.map {
                        withContext(Dispatchers.IO) {
                            async { it.convert(room.participantsUid.count()) }
                        }.await()
                    }
                })
            } catch (e: Exception) {
                UiState.Error(e)
            }
    }

    suspend fun observeMessage(room: ChatRoomEntity) {
        chatRepository.observeNewMessage(room) { newMessage ->
            viewModelScope.launch(Dispatchers.IO) {
                readMessage(newMessage)
            }
        }
    }

    private suspend fun readMessage(message: MessageEntity) {
        message.viewUsers.takeUnless { it.contains(uid) }?.also {
            val updatedMessage = message.copy(viewUsers = it + uid)
            chatRepository.updateMessage(updatedMessage)
        }
        setMessages(chatRoom.value)
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val imageUrl = selectedImage.value.let {
                if (it != Uri.EMPTY) imageRepository.uploadChatImage(it).getOrNull()?.toString()
                else null
            }

            MessageEntity(
                chatRoomId = chatRoom.value.key,
                text = text.ifEmpty { "사진" },
                img = imageUrl,
                authId = uid,
                viewUsers = listOf(uid)
            ).let {
                chatRepository.sendMessage(it)
                sendNotificationUseCase(it, chatRoom.value)
            }

            if (text.containsUrl()) {
                fetchUrlMetaData(text.extractUrl()).let { data ->
                    if (data is UrlMetaData) {
                        launch { sendEmbed(data) }
                    }
                }
            }
        }
    }

    private suspend fun sendEmbed(data: UrlMetaData) {
        MessageEntity(
            chatRoomId = chatRoom.value.key,
            text = "링크",
            embed = data,
            authId = uid,
            viewUsers = listOf(uid)
        ).let {
            chatRepository.sendMessage(it)
        }
    }

    private suspend fun MessageEntity.convert(users: Int) =
        if (authId == uid) convertMyMessage(users)
        else convertOtherMessage(users, authId ?: "")

    private fun MessageEntity.convertMyMessage(users: Int) = ChatRoomItem.MyMessage(
        key = key,
        text = text.toString(),
        img = img,
        time = registerDate.convertTime(),
        viewCount = (users - viewUsers.size),
        embed = embed
    )

    private suspend fun MessageEntity.convertOtherMessage(users: Int, authId: String) =
        userRepository.getUserDetails(authId).let {
            ChatRoomItem.OtherMessage(
                key = key,
                text = text.toString(),
                time = registerDate.convertTime(),
                img = img,
                viewCount = (users - viewUsers.size),
                profileUrl = it.getOrNull()?.photoUrl,
                name = it.getOrNull()?.name.toString(),
                embed = embed
            )
        }

    fun selectImg(uri: Uri?) {
        _selectedImage.value = uri ?: Uri.EMPTY
    }

    fun emptyImg() {
        _selectedImage.value = Uri.EMPTY
    }
}
