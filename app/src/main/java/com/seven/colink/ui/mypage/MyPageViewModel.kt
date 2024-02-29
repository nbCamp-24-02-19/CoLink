package com.seven.colink.ui.mypage

import androidx.lifecycle.ViewModel
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.sign.signup.model.SignUpUserModel

class MyPageViewModel : ViewModel() {
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