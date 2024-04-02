package com.seven.colink.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _topItems: MutableLiveData<List<TopItems>> = MutableLiveData(mutableListOf())
    val topItems: LiveData<List<TopItems>> get() = _topItems
    private val _promotionItems: MutableLiveData<MutableList<HomeAdapterItems.PromotionView>> = MutableLiveData()
    val promotionItems: LiveData<MutableList<HomeAdapterItems.PromotionView>> get() = _promotionItems
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading

    fun getTopItems(num : Int) {
//        getMiddlePromotionItem(2)
        _isLoading.value = true
        val getTopItemList: MutableList<TopItems> = mutableListOf()

        viewModelScope.launch {
            getTopItemList.clear()
            val repository = productRepository.getRecentPost(num)

            kotlin.runCatching {
                repository.forEach {
                    val topRecentItem = TopItems(it.imageUrl,it.projectId,it.registeredDate,
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

    fun getMiddlePromotionItem(num: Int) {
        val getMiddleItemList : MutableList<HomeAdapterItems.PromotionView> = mutableListOf()

        viewModelScope.launch {
            getMiddleItemList.clear()
            val repository = productRepository.getRecentPost(num)

            repository.forEach {
                val recentItem = MiddlePromotionItems(it.title,it.description,it.projectId,it.imageUrl,it.key)
                val item = HomeAdapterItems.PromotionView(recentItem)
                getMiddleItemList.add(item)
            }
            _promotionItems.value = getMiddleItemList
        }
    }
}