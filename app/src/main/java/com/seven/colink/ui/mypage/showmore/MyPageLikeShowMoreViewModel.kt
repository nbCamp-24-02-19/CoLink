package com.seven.colink.ui.mypage.showmore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.search.SearchModel
import com.seven.colink.util.convert.convertToDaysAgo
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyPageLikeShowMoreViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _likeModel = MutableLiveData<List<SearchModel>>()
    val likeModel: LiveData<List<SearchModel>> get() = _likeModel

    fun loadLikePost() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            val userDetails = userRepository.getUserDetails(currentUser.message)
            val likeList = userDetails.getOrNull()?.likeList
            val getLikeList = likeList?.map {
                getPost(it)?.convertSearchModel()
            }?.sortedByDescending { it?.registeredDate }
            _likeModel.value = getLikeList?.map {
                it!!.copy(registeredDate = it.registeredDate?.convertToDaysAgo())
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }

    private suspend fun PostEntity.convertSearchModel() =
        SearchModel(
            key = key,
            thumbnail = imageUrl,
            authId = withContext(Dispatchers.IO) {
                userRepository.getUserDetails(authId.toString()).getOrNull()?.name.toString()
            },
            title = title,
            status = status,
            groupType = groupType,
            description = description,
            tags = tags,
            registeredDate = registeredDate,
            views = views,
            likes = like
        )
}