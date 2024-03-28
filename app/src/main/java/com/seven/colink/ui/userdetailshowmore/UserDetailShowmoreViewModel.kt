package com.seven.colink.ui.userdetailshowmore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.userdetailshowmore.UserDetailShowmoreActivity.Companion.EXTRA_USER_KEY2
import com.seven.colink.util.convert.convertToDaysAgo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserDetailShowmoreViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    handle: SavedStateHandle,
): ViewModel() {

    private val _userDetailShowmore = MutableLiveData<List<UserDetailShowmoreModel>>()
    val userDetailShowmore : LiveData<List<UserDetailShowmoreModel>> = _userDetailShowmore
    private var userId: String

    init {
        userId = handle.get<String>(EXTRA_USER_KEY2)?: ""
        loadUserPost()
    }

    private fun loadUserPost(){
        viewModelScope.launch {
            val result = postRepository.getPostByAuthId(userId)
            result.onSuccess { post->
               _userDetailShowmore.postValue(post.map { it.convertPostEntity() })
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }

    private suspend fun PostEntity.convertPostEntity() = UserDetailShowmoreModel(
        key = key,
        title = title,
        ing = status,
        grouptype = groupType,
        time = registeredDate?.convertToDaysAgo(),
        description = description,
        name = withContext(Dispatchers.IO) {
            userRepository.getUserDetails(authId.toString()).getOrNull()?.name.toString()
        },
        image = imageUrl,
        tag = tags,
        view = views
    )
}