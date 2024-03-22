package com.seven.colink.ui.home.child

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.ui.home.TopItems
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeChildViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _bottomItems: MutableLiveData<List<BottomItems>> = MutableLiveData(mutableListOf())
    private val _isLoading = MutableLiveData<Boolean>()
    val bottomItems: LiveData<List<BottomItems>> get() = _bottomItems
    val isLoading : LiveData<Boolean> get() = _isLoading

    fun getBottomItems(num: Int,type : GroupType?) {
        _isLoading.value = true
        var getBottomItemList: MutableList<BottomItems> = mutableListOf()

        viewModelScope.launch {
            getBottomItemList.clear()
            val repository = postRepository.getRecentPost(num,type)

            kotlin.runCatching {
                repository.forEach {
                    while (true) {
                        if (getBottomItemList.size < num) {
                            var bottomRecentItem = BottomItems(it.groupType,it.title,it.description
                                ,it.tags,it.imageUrl,it.key,it.status,it.status)
                            getBottomItemList.add(bottomRecentItem)
                        }else if (getBottomItemList.size > num) {
                            getBottomItemList.removeAt(num +1)
                        }
                        break
                    }
                }
                _bottomItems.value = getBottomItemList
                _isLoading.value = false
            }.onFailure { exception ->
                Log.e("HomeChildViewModel", "error = $exception")
            }
        }
    }

    suspend fun getPost(key: String): PostEntity? {
        return postRepository.getPost(key).getOrNull()
    }
}