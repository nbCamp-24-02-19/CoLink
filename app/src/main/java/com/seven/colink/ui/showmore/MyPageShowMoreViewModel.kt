package com.seven.colink.ui.showmore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.util.convert.convertToDaysAgo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyPageShowMoreViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
): ViewModel() {
    private val _myPosts = MutableLiveData<List<MyPageShowMoreModel>>()
    val myPost: LiveData<List<MyPageShowMoreModel>> = _myPosts

    init {
        loadUserPost()
    }

    private fun loadUserPost(){
        viewModelScope.launch {
            val result = postRepository.getPostByAuthId(authRepository.getCurrentUser().message)
            result.onSuccess { post ->
                _myPosts.postValue(post.sortedByDescending { it.registeredDate }.map { it.convertPostEntity() })
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }


    private suspend fun PostEntity.convertPostEntity() = MyPageShowMoreModel(
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