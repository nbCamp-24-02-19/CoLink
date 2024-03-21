package com.seven.colink.ui.post.register.setgroup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetChatRoomUseCase
import com.seven.colink.domain.usecase.SendNotificationInviteUseCase
import com.seven.colink.ui.post.register.recommend.type.RecommendType
import com.seven.colink.util.model.MemberCard
import com.seven.colink.util.model.MemberInfo
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetGroupViewModel @Inject constructor(

) : ViewModel() {
    private val _onGroupEvent = MutableSharedFlow<Boolean>()
    val onGroupEvent = _onGroupEvent.asSharedFlow()
    fun onGroupEdit() {
        viewModelScope.launch {
            _onGroupEvent.emit(true)
        }
    }
}