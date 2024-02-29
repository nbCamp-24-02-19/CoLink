package com.seven.colink.ui.home

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
class HomeViewModel@Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
    private val _topItems : MutableLiveData<List<TopItems>> = MutableLiveData(mutableListOf())
    private val _bottomItems : MutableLiveData<List<BottomItems>> = MutableLiveData(mutableListOf())
    val topItems : LiveData<List<TopItems>> get() = _topItems
    val bottomItems : LiveData<List<BottomItems>> get() = _bottomItems

    suspend fun getTopItems(string: String) {
        var getTopItemList : MutableList<TopItems> = mutableListOf()
        var t :MutableLiveData<List<PostEntity>> = MutableLiveData()

        viewModelScope.launch {
            getTopItemList.clear()
            val tt = t.value
            val repository = postRepository.getPost(string)
            kotlin.runCatching {
                repository.getOrNull()?.
            }


        }
    }

}