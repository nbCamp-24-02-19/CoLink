package com.seven.colink.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.util.convert.convertToDaysAgo
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _userDetails = MutableLiveData<UiState<MyPageUserModel>>()
    private val _userPosts = MutableLiveData<List<MyPagePostModel>>()
    val userDetails: LiveData<UiState<MyPageUserModel>> = _userDetails
    val userPost: LiveData<List<MyPagePostModel>> = _userPosts

    init {
        loadUserDetails()
        loadUserPost()
    }

    fun loadUserDetails() {
        viewModelScope.launch {

            _userDetails.value = UiState.Loading
            _userDetails.value =
                try {
                    when (val currentUser = authRepository.getCurrentUser()) {
                        DataResultStatus.SUCCESS -> {
                            try {
                                val userDetailsResult =
                                    userRepository.getUserDetails(currentUser.message)
                                userDetailsResult.fold(
                                    onSuccess = { user ->
                                        user?.let { UiState.Success(it.convertUserEntity()) }
                                    },
                                    onFailure = { exception ->
                                        UiState.Error(exception)
                                    }
                                )
                            } catch (exception: Exception) {
                                UiState.Error(exception)
                            }
                        }

                        else -> UiState.Error(Exception(currentUser.message))
                    }
                } catch (e: Exception) {
                    UiState.Error(e)
                }
        }
    }

    fun loadUserPost() {
        viewModelScope.launch {
            val result = postRepository.getPostByAuthId(authRepository.getCurrentUser().message)
            result.onSuccess { post ->
                post.forEach {
                }

                _userPosts.postValue(post.sortedByDescending { it.registeredDate }
                    .map { it.convertPostEntity() })

            }
        }
    }

    fun updateSkill(skill: String) {
        viewModelScope.launch {
            val result = userRepository.getUserDetails(authRepository.getCurrentUser().message)
            result.onSuccess {
                it?.copy(skill = it.skill?.plus(skill)?.distinct())
                    ?.let { it1 -> userRepository.registerUser(it1) }
                loadUserDetails()
            }
        }
    }

    fun removeSkill(skill: String) {

        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser().message
            val userDetailsResult = userRepository.getUserDetails(currentUser)
            userDetailsResult.onSuccess { user ->
                val updatedSkills = user?.skill?.filter { it != skill }
                Log.d("Tag", "updateSkills = ${updatedSkills}")
                val updatedUser = user?.copy(skill = updatedSkills)
                updatedUser?.let {
                    userRepository.registerUser(it)
                    loadUserDetails()
                }
            }.onFailure { exception ->
                Log.e("ViewModel", "Error fetching user details", exception)
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    private fun PostEntity.convertPostEntity() = MyPagePostModel(
        key = key,
        title = title,
        ing = status,
        grouptype = groupType,
        time = registeredDate?.convertToDaysAgo()
    )


    private fun UserEntity.convertUserEntity() = MyPageUserModel(
        name = name,
        email = email,
        profile = photoUrl,
        mainSpecialty = specialty,
        specialty = specialty,
        skill = skill,
        level = level,
        info = info,
        git = git,
        blog = blog,
        link = link,
        score = grade
    )

}