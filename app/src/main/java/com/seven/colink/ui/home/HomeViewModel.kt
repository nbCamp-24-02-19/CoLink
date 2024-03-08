package com.seven.colink.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _topItems: MutableLiveData<List<TopItems>> = MutableLiveData(mutableListOf())
    val topItems: LiveData<List<TopItems>> get() = _topItems

    fun getTopItems(num : Int) {
        var getTopItemList: MutableList<TopItems> = mutableListOf()

        viewModelScope.launch {
            getTopItemList.clear()
            val repository = postRepository.getRecentPost(num)

            kotlin.runCatching {
                repository.forEach {
//                    var topRecentItem = TopItems(it.imageUrl,it.recruitInfo,it.registeredDate,
//                        it.title,it.key)
//                    getTopItemList.add(topRecentItem)
                    while (true) {
                        if (getTopItemList.size < num) {
                            var topRecentItem = TopItems(it.imageUrl,it.recruitInfo,it.registeredDate,
                                it.title,it.key)
                            getTopItemList.add(topRecentItem)
                        }else if (getTopItemList.size > num) {
                            getTopItemList.removeAt(num +1)
                        }
                        break
                    }
                }
                _topItems.value = getTopItemList

            }.onFailure { exception ->
                Log.e("HomeViewModel","데이터를 불러오지 못 했습니다 $exception")
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }
}