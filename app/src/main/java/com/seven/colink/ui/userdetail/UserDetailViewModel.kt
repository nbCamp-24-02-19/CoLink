package com.seven.colink.ui.userdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.mypage.MyPagePostModel
import com.seven.colink.ui.userdetail.UserDetailActivity.Companion.DETAIL_USER_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val handle: SavedStateHandle,
): ViewModel() {

    private val _userDetails = MutableLiveData<UserDetailModel>()
    private val _userDetailPosts = MutableLiveData<List<UserDetailPostModel>>()
    val userDetails: LiveData<UserDetailModel> = _userDetails
    val userDetailPost: LiveData<List<UserDetailPostModel>> = _userDetailPosts

    init {
        loadUserDetails()
        loadUserPost()
    }

    private fun loadUserDetails(){
        viewModelScope.launch {
            val result = userRepository.getUserDetails(handle.get<String>(DETAIL_USER_KEY))
            result.onSuccess { user->
                _userDetails.postValue(user?.convertUserEntity())
            }
        }
    }

    private fun loadUserPost() {
        viewModelScope.launch {
            val result = handle.get<String>(DETAIL_USER_KEY)
                ?.let { postRepository.getPostByAuthId(it) }
            result?.onSuccess { post ->
                _userDetailPosts.postValue(post.map { it.convertPostEntity() })
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
        userscore = grade,
        userLink = link
    )

    private fun PostEntity.convertPostEntity() = UserDetailPostModel(
        key = key,
        title = title,
        ing = status,
        grouptype = groupType,
        time = registeredDate
    )

}