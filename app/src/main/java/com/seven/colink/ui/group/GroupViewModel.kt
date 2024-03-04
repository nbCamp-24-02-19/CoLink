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

    private var entity: PostEntity? = null
    private var entityList: List<PostEntity> = emptyList()

    private val _groupData = MutableLiveData<List<GroupData>>()
    val groupData: LiveData<List<GroupData>> get() = _groupData

    private val _joinGroup = MutableLiveData<List<PostEntity>?>()
    val joinGroup: LiveData<List<PostEntity>?> get() = _joinGroup

    init {
        viewModelScope.launch {
            getInPost()
        }
    }

    fun itemUpdate() {
        viewModelScope.launch {
            val items = mutableListOf<GroupData>()

            items.add(getTitle())
            items.add(getList())
            items.add(getAdd())
            items.add(getWant())

            _groupData.value = items
            Log.d("Group", "GroupData.value = ${_groupData.value}")
        }
    }

    fun createGroup(groupList: List<GroupEntity>?) =
        groupList?.map {
            GroupData.GroupItem(
                group = it
            )
        } ?: emptyList()

    suspend fun getInPost() {
//        var result: PostEntity? = null
        val currentUser = authRepository.getCurrentUser()
        val result = groupRepository.getGroupByContainUserId(currentUser.message).getOrNull()
        _groupData.value = result?.map {
            it.convertGroupList()
        }
        Log.d("Group", "result = ${result}")


//            if (currentUser == DataResultStatus.SUCCESS) {
//            val userKey = postRepository.getPostByContainUserId(currentUser.message)
//                .getOrNull()

//            result = userKey?.firstOrNull()!!
//            _joinGroup.value = userKey
//
//            Log.d("Group", "userkey = ${userKey}")
//            Log.d("Group", "result = ${result}")
//            Log.d("Group", "currentUser = ${currentUser.message}")
//        } else {
//            Log.d("Group", "Failed")
//        }
//        Log.d("Group", "result2 = ${result}")
//        Log.d("Group", "_joinGroup.value = ${_joinGroup.value}")
    }

    private fun GroupEntity.convertGroupList() =
        GroupData.GroupList(
            key = key,
            groupType = groupType,
            thumbnail = imageUrl,
            projectName = title,
            days = 123,
            description = description,
            tags = tags
        )

    private fun getTitle() = GroupData.GroupTitle(
        title = "참여 중인 그룹"
    )

    private fun getList() = GroupData.GroupList(
        key = entity?.key,
        groupType = entity?.groupType,
        thumbnail = entity?.imageUrl,
        projectName = entity?.title,
        days = 123,
        description = entity?.description,
        tags = entity?.tags
    )

    private fun getAdd() = GroupData.GroupAdd(
        addGroupImage = R.drawable.ic_add,
        addGroupText = "새 그룹 추가하기",
        appliedGroup = "지원한 그룹"
    )

    private fun getWant() = GroupData.GroupWant(
        groupType = GroupType.PROJECT,
        title = "영화 커뮤니티 서비스 프로젝트 모집합니다",
        description = "영화 커뮤니티 서비스 프로젝트를 함께하실 안드로이드 개발자를 모십니다.",
        kind = "나는뉴비",
        lv = "4이상",
        img = R.drawable.img_dialog_study
    )
}