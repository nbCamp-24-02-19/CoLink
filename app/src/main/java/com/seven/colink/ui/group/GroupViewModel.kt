package com.seven.colink.ui.group

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val recruitRepository: RecruitRepository,
) : ViewModel() {

    private val _groupData = MutableLiveData<List<GroupData>?>()

    val groupData: LiveData<List<GroupData>?> get() = _groupData

    private val _joinList = MutableLiveData<List<GroupData.GroupList>?>()
    val joinList: LiveData<List<GroupData.GroupList>?> get() = _joinList

    private val _wantList = MutableLiveData<List<GroupData.GroupWant>?>()
    val wantList: LiveData<List<GroupData.GroupWant>?> get() = _wantList

    private val _checkLogin = MutableLiveData(false)
    val checkLogin: LiveData<Boolean> get() = _checkLogin

    fun itemUpdate() {
        viewModelScope.launch {
            val items = mutableListOf<GroupData>()

            items.add(getTitle())
            if (checkLogin.value == true) {
                if (joinList.value.isNullOrEmpty()) {
                    items.add(getEmptyJoinList())
                } else {
                    joinList.value?.map { items.add(it) }
                }
            }
            else {
                items.add(getEmptyJoinList())
            }
            items.add(getAdd())
            if (wantList.value.isNullOrEmpty()) {
                items.add(getEmptyWantList())
            } else {
                wantList.value?.map { items.add(it) }
            }

            _groupData.value = items
        }
    }

    fun getCurrentUser(){
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _checkLogin.value = currentUser == DataResultStatus.SUCCESS
        }
    }

    suspend fun getInPost() {
        val currentUser = authRepository.getCurrentUser()
        val result = groupRepository.getGroupByContainUserId(currentUser.message).getOrNull()?.map {
            it.convertGroupList()
        }
        _joinList.value = result
    }

    suspend fun getInApplicationInfo() {
        viewModelScope.launch {
            _wantList.value = recruitRepository.getApplicationInfoByUid(
                authRepository.getCurrentUser().message
            ).getOrNull()?.map {
                async {
                    it.recruitId?.let { recruitId ->
                        recruitRepository.getRecruit(recruitId)?.postId?.let { postId ->
                            postRepository.getPost(
                                postId
                            ).getOrNull()?.convertGroupWant()
                        }
                    }
                }
            }?.awaitAll()?.filterNotNull()
        }
    }

    private fun GroupEntity.convertGroupList() =
        GroupData.GroupList(
            key = key,
            groupType = groupType,
            thumbnail = imageUrl,
            projectName = teamName,
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
            tags = tags,
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

    private fun getEmptyJoinList() = GroupData.GroupEmpty(
        text = "참여 중인 그룹이 없습니다."
    )

    private fun getEmptyWantList() = GroupData.GroupEmpty(
        text = "지원한 그룹이 없습니다."
    )
}