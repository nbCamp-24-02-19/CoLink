package com.seven.colink.ui.group

import android.util.Log
import androidx.constraintlayout.widget.ConstraintSet
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
import com.seven.colink.util.convert.convertCalculateDays
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _groupData = MutableLiveData<List<GroupData>?>()

    val groupData: LiveData<List<GroupData>?> get() = _groupData

    private val _joinList = MutableLiveData<List<GroupData.GroupList>?>()
    val joinList: LiveData<List<GroupData.GroupList>?> get() = _joinList

    private val _wantList = MutableLiveData<List<GroupData.GroupWant>?>()
    val wantList: LiveData<List<GroupData.GroupWant>?> get() = _wantList

    init {
        viewModelScope.launch {
            getInPost()
            itemUpdate()
        }
    }

    private fun itemUpdate() {
        viewModelScope.launch {
            val items = mutableListOf<GroupData>()

            items.add(getTitle())
            if (joinList.value.isNullOrEmpty()) {
                items.add(getEmptyJoinList())
            } else {
                joinList.value?.map { items.add(it) }
            }
            items.add(getAdd())
            if (wantList.value.isNullOrEmpty()) {
                items.add(getEmptyWantList())
            } else {
                wantList.value?.map { items.add(it) }
            }

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
        _joinList.value = result
    }

    private fun GroupEntity.convertGroupList() =
        GroupData.GroupList(
            key = key,
            groupType = groupType,
            thumbnail = imageUrl,
            projectName = title,
            days = if (startDate.isNullOrEmpty()) {
                "모집중"
            } else {
                startDate.convertCalculateDays()
            },
            description = description,
            tags = tags,
            memberIds = memberIds
        )

    private fun PostEntity.convertGroupWant() =
        GroupData.GroupWant(
            key = key,
            groupType = groupType,
            title = title,
            description = description,
            kind = authId,
            img = imageUrl
        )

    private fun getTitle() = GroupData.GroupTitle(
        title = "참여 중인 그룹"
    )

    private fun getAdd() = GroupData.GroupAdd(
        addGroupImage = R.drawable.ic_add,
        addGroupText = "모집 글 작성하기",
        appliedGroup = "지원한 그룹"
    )

    private fun getWant() = GroupData.GroupWant(
        key = "",
        groupType = GroupType.PROJECT,
        title = "영화 커뮤니티 서비스 프로젝트 모집합니다",
        description = "영화 커뮤니티 서비스 프로젝트를 함께하실 안드로이드 개발자를 모십니다.",
        kind = "나는뉴비",
        img = "https://firebasestorage.googleapis.com/v0/b/colink-a7c3a.appspot.com/o/img%2F2ad7abe5-7945-47ba-b5ca-611278836783.jpg?alt=media&token=a1445ac5-b90f-4af3-b2de-9e07052bb497"
    )

    private fun getEmptyJoinList() = GroupData.GroupEmpty(
        img = R.drawable.img_temporary,
        text = "참여 중인 그룹이 없습니다."
    )

    private fun getEmptyWantList() = GroupData.GroupEmpty(
        img = R.drawable.img_dialog_study,
        text = "지원한 그룹이 없습니다."
    )
}