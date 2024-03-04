package com.seven.colink.ui.mypage

import androidx.lifecycle.ViewModel
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
): ViewModel() {
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