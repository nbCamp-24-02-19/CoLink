package com.seven.colink.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.util.convert.convertToDaysAgo
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _searchModel = MutableLiveData<List<SearchModel>>()
    val searchModel: LiveData<List<SearchModel>> get() = _searchModel

    private val _searchGroupState = MutableLiveData("ALL")
    val searchGroupState: LiveData<String> get() = _searchGroupState

    private val _searchRecruitState = MutableLiveData("ALL")
    val searchRecruitState: LiveData<String> get() = _searchRecruitState

    init {
        doSearch("")
    }

    fun doSearch(query: String) {
        val groupType: GroupType? = when (searchGroupState.value) {
            "ALL" -> null
            "PROJECT" -> GroupType.PROJECT
            "STUDY" -> GroupType.STUDY
            else -> {
                _searchModel.value = emptyList()
                return
            }
        }
        val recruitType: ProjectStatus? = when (searchRecruitState.value) {
            "ALL" -> null
            "RECRUIT" -> ProjectStatus.RECRUIT
            "END" -> ProjectStatus.END
            else -> {
                _searchModel.value = emptyList()
                return
            }
        }

        viewModelScope.launch {
            try {
                val result =
                    postRepository.searchQuery(query, groupType, recruitType).sortedByDescending {
                        it.registeredDate
                    }.map {
                        Log.d("qweew", "$it")
                        it.convertSearchModel()
                    }
                _searchModel.postValue(result)
            } catch (e: Exception) {
                Log.e("doSearch", "Error during search", e)
            }
            Log.d("doSearch", "SearchValue = ${groupType} , ${recruitType}")
            Log.d("doSearch", "SearchValueResult = ${_searchModel.value}")

        }
    }

    suspend fun getPost(key: String): PostEntity? {
        val post = postRepository.getPost(key).getOrNull()
        Log.d("getPost", "post = ${post}")
        return post
    }

    fun setGroupBoth(query: String) {
        _searchGroupState.value = "ALL"
        doSearch(query)
    }

    fun setProjectFilter(query: String) {
        _searchGroupState.value = "PROJECT"
        doSearch(query)
    }

    fun setStudyFilter(query: String) {
        _searchGroupState.value = "STUDY"
        doSearch(query)
    }

    fun setGroupNone(query: String) {
        _searchGroupState.value = "NONE"
        doSearch(query)
    }

    fun setRecruitBoth(query: String) {
        _searchRecruitState.value = "ALL"
        doSearch(query)
    }

    fun setRecruitFilter(query: String) {
        _searchRecruitState.value = "RECRUIT"
        doSearch(query)
    }

    fun setRecruitEndFilter(query: String) {
        _searchRecruitState.value = "END"
        doSearch(query)
    }

    fun setRecruitNone(query: String) {
        _searchRecruitState.value = "NONE"
        doSearch(query)
    }

    private suspend fun PostEntity.convertSearchModel() =
        SearchModel(
            key = key,
            authId = withContext(Dispatchers.IO) {
                userRepository.getUserDetails(authId.toString()).getOrNull()?.name.toString()
            },
            title = title,
            status = status,
            groupType = groupType,
            description = description,
            tags = tags,
            registeredDate =
            if (startDate.isNullOrEmpty()) {
                "모집중"
            } else {
                startDate.convertToDaysAgo()
            },
            views = views
        )
}
