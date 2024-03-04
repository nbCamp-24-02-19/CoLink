package com.seven.colink.ui.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.usecase.GetChatRoomUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatRoomUsecase: GetChatRoomUsecase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _a = MutableLiveData<String?>(null)
    val a: LiveData<String?> = _a
    fun createChat() {
        viewModelScope.launch {
            _a.value = withContext(Dispatchers.IO) {
                getChatRoomUsecase("dh6BeQHBj7QNrbnm5Y4gM224Kqm2").key
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}