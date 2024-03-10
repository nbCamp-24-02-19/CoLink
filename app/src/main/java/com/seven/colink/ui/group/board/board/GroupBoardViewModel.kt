package com.seven.colink.ui.group.board.board

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupBoardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val postUseCase: GetPostUseCase
) : ViewModel() {
    private val _uiStateList = MutableLiveData<List<GroupBoardItem>?>()
    val uiStateList: LiveData<List<GroupBoardItem>?> get() = _uiStateList

    private val _entity = MutableLiveData(GroupBoardUiState.init())
    val entity: LiveData<GroupBoardUiState?> get() = _entity

    fun setEntity(key: String) = viewModelScope.launch {
        val groupEntity = key.let { groupRepository.getGroupDetail(it).getOrNull() }
        _entity.value = entity.value?.copy(groupEntity = groupEntity)
        entity.value?.let {
            checkCurrentUser(it.groupEntity)
            groupContentItems()
        }
    }

    private fun checkCurrentUser(groupEntity: GroupEntity?) = viewModelScope.launch {
        val currentUser = getCurrentUser()
        _entity.value = entity.value?.copy(
            buttonUiState =
            if (groupEntity?.authId == currentUser) ContentButtonUiState.Manager
            else ContentButtonUiState.User
        )
    }

    private suspend fun getCurrentUser(): String = authRepository.getCurrentUser().message

    private fun groupContentItems() = viewModelScope.launch {
        val items = mutableListOf<GroupBoardItem>()
        val postEntity =
            entity.value?.groupEntity?.postKey?.let { postUseCase(it) }
        Log.d("1234", "${postEntity?.recruit}")
        entity.value?.groupEntity?.let { entity ->
            items.add(
                GroupBoardItem.GroupItem(
                    key = entity.key,
                    authId = entity.authId,
                    teamName = entity.teamName,
                    imageUrl = entity.imageUrl.orEmpty(),
                    status = entity.status,
                    groupType = entity.groupType,
                    description = entity.description,
                    tags = entity.tags,
                    startDate = entity.startDate,
                    endDate = entity.endDate,
                    isOwner = entity.authId == getCurrentUser()
                )
            )
            items.add(
                GroupBoardItem.TitleItem(
                    titleRes = R.string.group_post_title,
                    viewType = GroupContentViewType.POST_ITEM
                )
            )
            postEntity?.let { items.add(GroupBoardItem.PostItem(it)) }
            items.add(
                GroupBoardItem.TitleItem(
                    titleRes = R.string.project_member_info,
                    viewType = GroupContentViewType.MEMBER_ITEM
                )
            )
            items.addAll(entity.createMember())
            _uiStateList.value = items
        }
    }

    private suspend fun GroupEntity.createMember(): List<GroupBoardItem> {
        val memberItems = mutableListOf<GroupBoardItem>()
        var leaderTitleAdded = false
        val memberIdsSet = memberIds.toSet()

        for (memberId in memberIdsSet) {
            val userEntity = userRepository.getUserDetails(memberId).getOrNull()

            if (userEntity != null) {
                val isLeader = authId == memberId

                if (isLeader && !leaderTitleAdded) {
                    memberItems.add(GroupBoardItem.SubTitleItem("리더"))
                } else if (!isLeader && !leaderTitleAdded) {
                    memberItems.add(GroupBoardItem.SubTitleItem("팀원"))
                    leaderTitleAdded = true
                }

                memberItems.add(GroupBoardItem.MemberItem(userEntity, false))
            }
        }

        return memberItems
    }

    fun onClickStatusButton() = viewModelScope.launch {
        // TODO 홍보 하기
    }
}