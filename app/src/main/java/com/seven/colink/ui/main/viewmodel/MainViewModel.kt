package com.seven.colink.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _userCheck = MutableSharedFlow<Pair<String?,String?>>()
    val userCheck = _userCheck.asSharedFlow()

    init {
        viewModelScope.launch {
            _userCheck.emit(
                userRepository.getUserDetails(
                    authRepository.getCurrentUser().message
                ).getOrNull()?.convert()?: return@launch
            )
        }
    }

    private fun UserEntity.convert() =
        name to photoUrl
}