package com.seven.colink.ui.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.usecase.GetChatRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatRoomUsecase: GetChatRoomUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _a = MutableLiveData<String?>(null)
    val a: LiveData<String?> = _a

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}