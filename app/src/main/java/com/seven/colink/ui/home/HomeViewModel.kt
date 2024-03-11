package com.seven.colink.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _topItems: MutableLiveData<List<TopItems>> = MutableLiveData(mutableListOf())
    val topItems: LiveData<List<TopItems>> get() = _topItems
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    fun getTopItems(num : Int) {
        _isLoading.value = true
        val getTopItemList: MutableList<TopItems> = mutableListOf()

        viewModelScope.launch {
            getTopItemList.clear()
            val repository = postRepository.getRecentPost(num)

            kotlin.runCatching {
                repository.forEach {
                    val topRecentItem = TopItems(it.imageUrl,it.recruitInfo,it.registeredDate,
                        it.title,it.key)
                    getTopItemList.add(topRecentItem)
                }

                if (getTopItemList.isNotEmpty()) {
                    val lastItem = getTopItemList.first().copy()
                    val firstItem = getTopItemList.last().copy()
                    getTopItemList.add(lastItem)
                    getTopItemList.add(0, firstItem)
                }
                _topItems.value = getTopItemList
                _isLoading.value = false
            }.onFailure { exception ->
                Log.e("HomeViewModel","데이터를 불러오지 못 했습니다 $exception")
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }
}