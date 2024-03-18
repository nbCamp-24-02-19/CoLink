package com.seven.colink.ui.sign.signin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
):ViewModel() {
    private val _entryType = MutableStateFlow(false)

    val entryType: StateFlow<Boolean> = _entryType

    private val _result = MutableSharedFlow<DataResultStatus>()
    val result = _result.asSharedFlow()
    init {
        viewModelScope.launch {
            _entryType.value = authRepository.getCurrentUser() == DataResultStatus.SUCCESS
        }
    }
    suspend fun signIn(email: String, password: String) {
        _result.emit(authRepository.signIn(email, password))
    }

    fun isSignIn(result: DataResultStatus) {
        _entryType.value = result == DataResultStatus.SUCCESS
    }
}