package com.seven.colink.ui.sign.signup.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.RegisterUserUseCase
import com.seven.colink.ui.sign.signup.SignUpActivity.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.ui.sign.signup.SignUpActivity.Companion.EXTRA_USER_ENTITY
import com.seven.colink.ui.sign.signup.model.SignUpUserModel
import com.seven.colink.ui.sign.signup.type.SignUpEntryType
import com.seven.colink.ui.sign.signup.type.SignUpUIState
import com.seven.colink.ui.sign.signup.valid.SignUpErrorMessage
import com.seven.colink.ui.sign.signup.valid.SignUpValidExtension.includeSpecialCharacters
import com.seven.colink.ui.sign.signup.valid.SignUpValidExtension.numAndEnglish
import com.seven.colink.ui.sign.signup.valid.SignUpValidExtension.validEmailServiceProvider
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val userRepository: UserRepository,
    private val handle: SavedStateHandle,
) : ViewModel() {

    private val _entryType = MutableStateFlow(SignUpEntryType.CREATE)
    val entryType: StateFlow<SignUpEntryType> = _entryType

    private val _uiStatus = MutableStateFlow(SignUpUIState.NAME)
    val uiStatus: StateFlow<SignUpUIState> = _uiStatus

    private val _userModel = MutableStateFlow(SignUpUserModel())
    val userModel: StateFlow<SignUpUserModel> = _userModel

    private val _errorMessage = MutableStateFlow(SignUpErrorMessage.DUMMY)
    val errorMessage: StateFlow<SignUpErrorMessage> = _errorMessage

    private val _registrationResult  = MutableSharedFlow<String>()
    val registrationResult = _registrationResult.asSharedFlow()

    private val _skills = MutableStateFlow(emptyList<String>())
    val skills: StateFlow<List<String>> = _skills

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

            SignUpUIState.NAME -> {
                if (text.includeSpecialCharacters()) {
                    SignUpErrorMessage.NAME
                } else {
                    _uiStatus.value = SignUpUIState.EMAIL
                    _userModel.value = _userModel.value.copy(name = text)
                    SignUpErrorMessage.PASS
                }
            }

            else -> {
                SignUpErrorMessage.PASS
            }
        }
    }

    fun checkValid(state: SignUpUIState, text1: String, text2: String) {
        viewModelScope.launch {
            _errorMessage.value = SignUpErrorMessage.DUMMY
            _errorMessage.value = when (state) {
                SignUpUIState.EMAIL -> {
                    if (text1.numAndEnglish()) {
                        if (text2.validEmailServiceProvider()) {
                            if (userRepository.checkUserEmail("$text1@$text2").not())
                                _uiStatus.value = SignUpUIState.PASSWORD
                            _userModel.value = _userModel.value.copy(email = "$text1@$text2")
                            SignUpErrorMessage.PASS
                        } else SignUpErrorMessage.DUPLICATE_EMAIL
                    } else SignUpErrorMessage.EMAIL
                }

                SignUpUIState.PASSWORD -> {
                    if (text1 == text2) {
                        _uiStatus.value = SignUpUIState.PROFILE
                        _userModel.value = _userModel.value.copy(password = text1)
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

    fun checkValid(map: Map<String, Any?>) {
        _errorMessage.value = SignUpErrorMessage.DUMMY
        map.forEach { (key, value) ->
            val isValueValid = when (key) {
                "mainSpecialty", "specialty" -> value is String && value.isNotEmpty()
                "level" -> value is Int
                "info", "git", "blog", "link" -> true
                else -> false
            }

            if (!isValueValid) {
                _errorMessage.value = when (key) {
                    "mainSpecialty", "specialty" -> SignUpErrorMessage.SPECIALTY
                    "level" -> SignUpErrorMessage.LEVEL
                    else -> SignUpErrorMessage.DUMMY
                }
                return@forEach
            } else {
                _userModel.value = when (key) {
                    "mainSpecialty" -> _userModel.value.copy(mainSpecialty = value as String)
                    "specialty" -> _userModel.value.copy(specialty = value as String)
                    "skills" -> _userModel.value.copy(skill = value as List<String>)
                    "level" -> _userModel.value.copy(level = value as Int)
                    "info" -> _userModel.value.copy(info = value as? String)
                    "git" -> _userModel.value.copy(git = value as? String)
                    "blog" -> _userModel.value.copy(blog = value as? String)
                    "link" -> _userModel.value.copy(link = value as? String)
                    else -> _userModel.value
                }
            }
        }
        if (skills.value.isEmpty()){
            _errorMessage.value = SignUpErrorMessage.SKILL
        }else {
            _userModel.value = _userModel.value.copy(skill = skills.value)
        }
        if (_errorMessage.value == SignUpErrorMessage.DUMMY) {
            _errorMessage.value = SignUpErrorMessage.PASS
            registerUser()
        }
    }

    private fun registerUser() = viewModelScope.launch {
        when (registerUserUseCase(userModel.value.convertUserEntity())){
            DataResultStatus.SUCCESS -> _registrationResult.emit("등록 성공")
            DataResultStatus.FAIL -> _registrationResult.emit("등록 실패")
        }
    }

    private fun SignUpUserModel.convertUserEntity() = UserEntity(
        name = name,
        email = email,
        password = password,
        mainSpecialty = mainSpecialty,
        specialty = specialty,
        skill = skill,
        level = level,
        info = info,
        git = git,
        blog = blog,
        link = link,
    )

    fun addSkill(skill: String) {
        if (skills.value.contains(skill).not()) {
            _skills.value = skills.value + skill
        }
    }

    fun removeSkill(skill: String) {
        _skills.value = skills.value - skill
    }
}

