package com.seven.colink.ui.home.child

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.ui.home.BottomItems
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeChildViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val groupRepository: GroupRepository,
) : ViewModel() {

    private val _bottomItems: MutableLiveData<List<BottomItems>> = MutableLiveData(mutableListOf())
    private val _isLoading = MutableLiveData<Boolean>()
    val bottomItems: LiveData<List<BottomItems>> get() = _bottomItems
    val isLoading : LiveData<Boolean> get() = _isLoading

    fun getBottomItems(num: Int,type : GroupType?) {
        _isLoading.value = true
        val getBottomItemList: MutableList<BottomItems> = mutableListOf()

        viewModelScope.launch {
            getBottomItemList.clear()
            val repository = postRepository.getRecentPost(num,type)

            kotlin.runCatching {
                repository.forEach {
                    while (true) {
                        if (getBottomItemList.size < num) {
                            val bottomRecentItem = BottomItems(typeId = it.groupType, title = it.title, des = it.description,
                                kind = it.tags, img = it.imageUrl, key = it.key)
                            getBottomItemList.add(bottomRecentItem)
                            getBottomItemList.forEachIndexed { index, item ->
                                val key = item.key
                                val group = key?.let { items -> groupRepository.getGroupDetail(items) }?.getOrNull()
                                val updateItem = item.copy(blind = group?.status, complete = group?.status)
                                getBottomItemList[index] = updateItem
                            }
                            val endStatusItems = getBottomItemList.filter { end -> end.complete == ProjectStatus.END }
                            if (endStatusItems.isNotEmpty()) {
                                getBottomItemList.removeAll(endStatusItems)
                                getBottomItemList.addAll(endStatusItems)
                            }
                        }else if (getBottomItemList.size > num) {
                            getBottomItemList.forEachIndexed { index, remove ->
                                if (remove.complete == ProjectStatus.END) {
                                    getBottomItemList.removeAt(index)
                                }else{
                                    getBottomItemList.removeAt(num +1)
                                }
                            }
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