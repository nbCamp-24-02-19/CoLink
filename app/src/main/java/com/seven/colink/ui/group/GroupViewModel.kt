package com.seven.colink.ui.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.util.Constants
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private var entity: GroupData.GroupList? = null
    private var entityList: List<GroupData.GroupList>? = emptyList()

    private val _groupData = MutableLiveData<List<GroupData>?>()
    val groupData: LiveData<List<GroupData>?> get() = _groupData

    init {
        viewModelScope.launch {
            getInPost()
            itemUpdate()
        }
    }

    fun itemUpdate() {
        viewModelScope.launch {
            val items = mutableListOf<GroupData>()

            items.add(getTitle())
            entityList?.map { items.add(it) }
            items.add(getAdd())
            items.add(getWant())

            _groupData.value = items
            Log.d("Group", "GroupData.value = ${_groupData.value}")
        }
    }

    private suspend fun getInPost() {
        val currentUser = authRepository.getCurrentUser()
        val result = groupRepository.getGroupByContainUserId(currentUser.message).getOrNull()?.map {
            it.convertGroupList()
        }
        Log.d("Group", "result1 = ${result}")
        entityList = result
    }

    private fun GroupEntity.convertGroupList() =
        GroupData.GroupList(
            key = key,
            groupType = groupType,
            thumbnail = imageUrl,
            projectName = title,
            days = registeredDate,
            description = description,
            tags = tags,
            memberIds = memberIds
        )

    private fun getTitle() = GroupData.GroupTitle(
        title = "참여 중인 그룹"
    )

//    private fun getList() = GroupData.GroupList(
//        key = entity?.key,
//        groupType = entity?.groupType,
//        thumbnail = entity?.imageUrl,
//        projectName = entity?.title,
//        days = entity?.registeredDate,
//        description = entity?.description,
//        tags = entity?.tags
//    )

    private fun getAdd() = GroupData.GroupAdd(
        addGroupImage = R.drawable.ic_add,
        addGroupText = "새 그룹 추가하기",
        appliedGroup = "지원한 그룹"
    )

    private fun getWant() = GroupData.GroupWant(
        key = "",
        groupType = GroupType.PROJECT,
        title = "영화 커뮤니티 서비스 프로젝트 모집합니다",
        description = "영화 커뮤니티 서비스 프로젝트를 함께하실 안드로이드 개발자를 모십니다.",
        kind = "나는뉴비",
        lv = "4이상",
        img = R.drawable.img_dialog_study
    )
}