package com.seven.colink.ui.sign.signup.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.ui.sign.signup.SignUpActivity.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.ui.sign.signup.SignUpActivity.Companion.EXTRA_USER_ENTITY
import com.seven.colink.ui.sign.signup.model.SignUpUserModel
import com.seven.colink.ui.sign.signup.type.SignUpEntryType
import com.seven.colink.ui.sign.signup.type.SignUpUIState
import com.seven.colink.ui.sign.signup.valid.SignUpErrorMessage
import com.seven.colink.ui.sign.signup.valid.SignUpValidExtension.includeSpecialCharacters
import com.seven.colink.ui.sign.signup.valid.SignUpValidExtension.numAndEnglish
import com.seven.colink.ui.sign.signup.valid.SignUpValidExtension.validEmailServiceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val _entryType = MutableStateFlow(SignUpEntryType.CREATE)
    val entryType: StateFlow<SignUpEntryType> = _entryType

    private val _uiStatus = MutableStateFlow(SignUpUIState.ID)
    val uiStatus: StateFlow<SignUpUIState> = _uiStatus

    private val _userModel = MutableStateFlow(SignUpUserModel())
    val userModel: StateFlow<SignUpUserModel> = _userModel

    private val _errorMessage = MutableStateFlow(SignUpErrorMessage.DUMMY)
    val errorMessage: StateFlow<SignUpErrorMessage> = _errorMessage

    init {
        _entryType.value = handle.get<SignUpEntryType>(EXTRA_ENTRY_TYPE) ?: SignUpEntryType.CREATE
        _userModel.value = handle.get<SignUpUserModel>(EXTRA_USER_ENTITY) ?: SignUpUserModel()
    }

    fun updateUiState(status: SignUpUIState) {
        _uiStatus.value = status
    }

    fun checkValid(state: SignUpUIState, text: String) {
        _errorMessage.value = SignUpErrorMessage.DUMMY
        _errorMessage.value = when (state) {
            SignUpUIState.ID -> {
                if (text.includeSpecialCharacters() || !text.numAndEnglish()) {
                    SignUpErrorMessage.ID
                } else {
                    _uiStatus.value = SignUpUIState.NAME
                    SignUpErrorMessage.PASS
                }
            }

            SignUpUIState.NAME -> {
                if (text.includeSpecialCharacters()) {
                    SignUpErrorMessage.NAME
                } else {
                    _uiStatus.value = SignUpUIState.EMAIL
                    SignUpErrorMessage.PASS
                }
            }

            else -> {
                SignUpErrorMessage.PASS
            }
        }
    }

    fun checkValid(state: SignUpUIState, text1: String, text2: String) {
        _errorMessage.value = SignUpErrorMessage.DUMMY
        _errorMessage.value = when (state) {
            SignUpUIState.EMAIL -> {
                if (text1.numAndEnglish()) {
                    if (text2.validEmailServiceProvider()) {
                        _uiStatus.value = SignUpUIState.PASSWORD
                        SignUpErrorMessage.PASS
                    } else SignUpErrorMessage.EMAIL
                } else SignUpErrorMessage.EMAIL
            }

            SignUpUIState.PASSWORD -> {
                if (text1 == text2) {
                    _uiStatus.value = SignUpUIState.PROFILE
                    SignUpErrorMessage.PASS
                } else {
                    SignUpErrorMessage.PASSWORD_PASSWORD
                }
            }

            else -> {
                SignUpErrorMessage.PASS
            }
        }
    }
}

