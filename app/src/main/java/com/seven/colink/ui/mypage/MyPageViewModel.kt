package com.seven.colink.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.sign.signup.model.SignUpUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    init {
        viewModelScope.launch {
            val user = userRepository.getUserDetails(authRepository.getCurrentUser().message)
            Log.d("tag", "ViewModel user = ${user}")
        }
    }
    // TODO: Implement the ViewModel

    private fun MyPageUserModel.convertUserEntity() = UserEntity(
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

}