package com.seven.colink.ui.search

import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.ui.post.PostActivity
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
    private val _searchModel = MutableLiveData<List<SearchModel>>()
    val searchModel: LiveData<List<SearchModel>> get() = _searchModel

    private val _searchGroupState = MutableLiveData("ALL")
    val searchGroupState: LiveData<String> get() = _searchGroupState

    private val _searchRecruitState = MutableLiveData("ALL")
    val searchRecruitState: LiveData<String> get() = _searchRecruitState

    fun doSearch(query: String) {
        val groupType: GroupType? = when (searchGroupState.value) {
            "ALL" -> null
            "PROJECT" -> GroupType.PROJECT
            "STUDY" -> GroupType.STUDY
            else -> return
        }
        val recruitType: ProjectStatus? = when (searchRecruitState.value) {
            "ALL" -> null
            "RECRUIT" -> ProjectStatus.RECRUIT
            "END" -> ProjectStatus.END
            else -> return
        }

        viewModelScope.launch {
            _searchModel.value = postRepository.searchQuery(query, groupType, recruitType).map {
                it.convertSearchModel()
            }
            Log.d("doSearch", "SearchValue = ${groupType} , ${recruitType}")
        }
    }

    fun setGroupBoth(query: String){
        _searchGroupState.value = "ALL"
        doSearch(query)
    }
    fun setProjectFilter(query: String) {
        _searchGroupState.value = "PROJECT"
        doSearch(query)
    }

    fun setStudyFilter(query: String){
        _searchGroupState.value = "STUDY"
        doSearch(query)
    }

    fun setGroupNone(query: String){
        _searchGroupState.value = "NONE"
        doSearch(query)
    }

    fun setRecruitBoth(query: String){
        _searchRecruitState.value = "ALL"
        doSearch(query)
    }

    fun setRecruitFilter(query: String){
        _searchRecruitState.value = "RECRUIT"
        doSearch(query)
    }

    fun setRecruitEndFilter(query: String){
        _searchRecruitState.value = "END"
        doSearch(query)
    }

    fun setRecruitNone(query: String){
        _searchRecruitState.value = "NONE"
        doSearch(query)
    }

}

private fun PostEntity.convertSearchModel() =
    SearchModel(
        key = key,
        authId = authId,
        title = title,
        status = status,
        groupType = groupType,
        description = description,
        tags = tags,
        registeredDate = registeredDate,
        views = views
    )