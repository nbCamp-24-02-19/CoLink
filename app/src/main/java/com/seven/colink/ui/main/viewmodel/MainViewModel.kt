package com.seven.colink.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ApplicationInfoEntity
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetChatRoomUseCase
import com.seven.colink.ui.main.model.ObserveAppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val recruitRepository: RecruitRepository,
    private val getChatRoomUseCase: GetChatRoomUseCase,
) : ViewModel() {

    private val _recruitApprove = MutableStateFlow<List<ObserveAppInfo>>(emptyList())
    val recruitApprove = _recruitApprove.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.registerToken()
        }

        viewModelScope.launch {
            recruitRepository.observeRecruitPending().collect { data ->
                _recruitApprove.value = data.map {
                    recruitRepository
                        .getRecruit(it.recruitId?: return@collect)?.postId
                        .let { post ->
                    it.convert(post!!) }}
            }
        }
    }
    fun getChatRoom(data: ObserveAppInfo) {
        viewModelScope.launch {
            getChatRoomUseCase(
                postId = data.postId,
                uid = data.uid
            )
        }
    }

    private fun ApplicationInfoEntity.convert(post: String) = ObserveAppInfo (
        postId = post,
        uid = userId!!,
    )
}
