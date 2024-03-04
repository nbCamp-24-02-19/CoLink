package com.seven.colink.ui.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
//    private val mContext: Context
) : ViewModel() {
    private val _topItems: MutableLiveData<List<TopItems>> = MutableLiveData(mutableListOf())
    private val _bottomItems: MutableLiveData<List<BottomItems>> = MutableLiveData(mutableListOf())
    val topItems: LiveData<List<TopItems>> get() = _topItems
    val bottomItems: LiveData<List<BottomItems>> get() = _bottomItems

    fun getTopItems(num : Int) {
        var getTopItemList: MutableList<TopItems> = mutableListOf()

        viewModelScope.launch {
            getTopItemList.clear()
            val repository = postRepository.getRecentPost(num)

            kotlin.runCatching {
//                val item = repository.getOrNull(num)
                repository.forEach {
                    var topRecentItem = TopItems(it.imageUrl,it.recruitInfo,it.registeredDate,
                        it.title,it.key)

                    getTopItemList.add(topRecentItem)

                }


//                Log.d("ViewModel","#aaa 여긴 뭐니? $item")
//                repository.forEach {
//                    var topRecentItem = TopItems(item.imageUrl,item.recruitInfo,item.startDate,
//                        item.title,item.key)
//                    Log.d("ViewModel","#aaa 데이터 가져온거 변환$topRecentItem")
//                    getTopItemList.add(topRecentItem)
//                    Log.d("ViewModel","#aaa 데이터 가져온거 집어넣기${getTopItemList[0]}")
//                }
                _topItems.value = getTopItemList

            }.onFailure { exception ->
                Log.e("HomeViewModel","#aaa error $exception")
//                Toast.makeText(mContext,"다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getBottomItems(num: Int) {
        var getBottomItemList: MutableList<BottomItems> = mutableListOf()

        viewModelScope.launch {
            getBottomItemList.clear()
            val repository = postRepository.getRecentPost(num)
            Log.d("ViewModel","#aaa 여긴 null 아니고$repository")
            kotlin.runCatching {
                repository.forEach {
                    var bottomRecentItem = BottomItems(it.groupType,it.title,it.description
                    ,it.tags,it.imageUrl,it.key,it.status,it.status)
                    Log.d("ViewModel","#aaa 데이터 가져온거 변환$bottomRecentItem")
                    getBottomItemList.add(bottomRecentItem)
                    Log.d("ViewModel","#aaa 데이터 가져온거 집어넣기${getBottomItemList[0]}")
                }
                _bottomItems.value = getBottomItemList
//                val item = repository.getOrNull(num)
//
//                repository.forEach {
////                    val lv = item?.recruit?.forEach { lv ->
////                        lv.
////                    }.toString()
//                    var bottomRecentItem = BottomItems(item?.groupType,item?.title,item?.description
//                    ,item?.tags,item?.imageUrl,item?.key,item?.status,item?.status)
//                    getBottomItemList.add(bottomRecentItem)
//                }
//                _bottomItems.value = getBottomItemList
            }.onFailure { exception ->
                Log.e("HomeViewModel", "#aaa error $exception")
//                Toast.makeText(mContext, "다음에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}