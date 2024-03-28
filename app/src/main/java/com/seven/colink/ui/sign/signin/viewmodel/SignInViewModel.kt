package com.seven.colink.ui.sign.signin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.FirebaseUser
import com.seven.colink.data.firebase.type.DataResult
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.KakaoRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.sign.signup.type.SignUpEntryType
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val kakaoRepository: KakaoRepository,
) : ViewModel() {

    private val _entryType = MutableStateFlow(false)

    val entryType: StateFlow<Boolean> = _entryType

    private val _result = MutableSharedFlow<DataResultStatus>()
    val result = _result.asSharedFlow()

    private val _updateEvent = MutableSharedFlow<SignUpEntryType>()
    val updateEvent = _updateEvent.asSharedFlow()

    fun signInCheck() {
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

    fun sendTokenByGoogle(idToken: String?) {
        viewModelScope.launch {
            if (idToken != null) {
                authRepository.registerUserByGoogle(idToken).let {
                    it as FirebaseUser
                    if (userRepository.getUserDetails(it.uid).getOrNull() == null) {
                        userRepository.registerUser(it.convert())
                        _updateEvent.emit(SignUpEntryType.UPDATE_PROFILE)
                    }
                }
            }
        }
    }

    fun getTokenByKakao() {
        viewModelScope.launch {
            kakaoRepository.kakaoLogin().getOrNull().let { token ->
                if (token != null) authRepository.getCustomToken(token).let { result ->
                    if (result is DataResult.Success) {
                        if (userRepository.getUserDetails(result.data.uid).getOrNull() == null) {
                            userRepository.registerUser(result.data.convert())
                            _updateEvent.emit(SignUpEntryType.UPDATE_PROFILE)
                        }
                    }
                }
            }
        }
    }
    private fun FirebaseUser.convert() = UserEntity(
        uid = uid,
        email = email,
        name = displayName,
        photoUrl = photoUrl.toString(),
    )
}
