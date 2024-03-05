package com.seven.colink.ui.userdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private val _userDetails = MutableLiveData<UserDetailModel>()
    val userDetails: LiveData<UserDetailModel> = _userDetails

    init {
        loadUserDetails()
    }

    private fun loadUserDetails(){
        viewModelScope.launch {
            val result = userRepository.getUserDetails(authRepository.getCurrentUser().message)
            result.onSuccess { user->
                _userDetails.postValue(user?.convertUserEntity())
            }
        }
    }


    private fun UserEntity.convertUserEntity() = UserDetailModel(
        userName = name,
        userProfile = photoUrl,
        userLevel = level,
        userMainSpecialty = mainSpecialty,
        userBlog = blog,
        userGit = git,
        userSkill = skill,
        userInfo = info,
        userscore = grade
    )

}