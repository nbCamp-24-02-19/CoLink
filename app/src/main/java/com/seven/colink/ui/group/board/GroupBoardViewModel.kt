package com.seven.colink.ui.group.board

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
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.post.content.model.ContentOwnerButtonUiState
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupBoardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    private val _uiStateList = MutableLiveData<List<GroupBoardItem>?>()
    val uiStateList: LiveData<List<GroupBoardItem>?> get() = _uiStateList

    private val _entity = MutableLiveData(GroupBoardUiState.init())
    val entity: LiveData<GroupBoardUiState?> get() = _entity

    private val _event: MutableLiveData<GroupContentEvent> = MutableLiveData()
    val event: LiveData<GroupContentEvent> get() = _event

    fun setEntity(key: String) = viewModelScope.launch {
        val groupEntity = key.let { groupRepository.getGroupDetail(it).getOrNull() }
        _entity.value = _entity.value?.copy(groupEntity = groupEntity)
        entity.value?.let {
            checkCurrentUser(it.groupEntity)
            groupContentItems()
        }
    }

    private fun checkCurrentUser(groupEntity: GroupEntity?) = viewModelScope.launch {
        val currentUser = getCurrentUser()
        _entity.value = _entity.value?.copy(
            buttonUiState =
            if (groupEntity?.authId == currentUser) ContentOwnerButtonUiState.Owner
            else ContentOwnerButtonUiState.User
        )
    }

    private suspend fun getCurrentUser(): String = authRepository.getCurrentUser().message

    private fun groupContentItems() = viewModelScope.launch {
        val items = mutableListOf<GroupBoardItem>()
        val postEntity =
            entity.value?.groupEntity?.postKey?.let { postRepository.getPost(it).getOrNull() }
        entity.value?.groupEntity?.let { entity ->
            items.add(entity.createGroupContentItem())
            items.add(R.string.group_post_title.createTitleItem(GroupContentViewType.POST_ITEM))
            postEntity?.let { items.add(it.createPostItem()) }
            items.add(R.string.project_member_info.createTitleItem(GroupContentViewType.MEMBER_ITEM))
            items.addAll(entity.createMember())
            _uiStateList.value = items
        }
    }


    private suspend fun GroupEntity.createGroupContentItem() = GroupBoardItem.GroupItem(
        key = key,
        authId = authId,
        teamName = teamName,
        imageUrl = imageUrl.orEmpty(),
        status = status,
        groupType = groupType,
        description = description,
        tags = tags,
        startDate = startDate,
        endDate = endDate,
        isOwner = authId == getCurrentUser()
    )

    private fun PostEntity.createPostItem() = GroupBoardItem.PostItem(
        post = this
    )

    private fun Int.createTitleItem(viewType: GroupContentViewType) = GroupBoardItem.TitleItem(
        titleRes = this,
        viewType = viewType
    )

    private suspend fun GroupEntity.createMember(): List<GroupBoardItem> {
        var leaderAdded = false
        var teamMemberAdded = false
        val processedItems = mutableListOf<GroupBoardItem>()

        memberIds.forEach { memberId ->
            userRepository.getUserDetails(memberId).getOrNull()?.let {
                val isLeader = memberId == entity.value?.groupEntity?.authId
                val title = when {
                    isLeader && !leaderAdded -> {
                        leaderAdded = true
                        "리더"
                    }
                    !isLeader && !teamMemberAdded -> {
                        teamMemberAdded = true
                        "팀원"
                    }
                    else -> null
                }

                if (title != null) {
                    processedItems.add(GroupBoardItem.SubTitleItem(title = title))
                }
                processedItems.add(GroupBoardItem.MemberItem(userInfo = it, role = title))
            }
        }

        return processedItems
    }


    fun onClickStatusButton() = viewModelScope.launch {
        val updateItem = when (entity.value?.groupEntity?.status) {
            ProjectStatus.RECRUIT -> ProjectStatus.START
            ProjectStatus.START -> ProjectStatus.END
            else -> return@launch
        }

        val result = entity.value?.groupEntity?.key?.let {
            groupRepository.updateGroupStatus(
                it,
                updateItem
            )
        }

        if (result == DataResultStatus.SUCCESS) {
            _uiStateList.value = _uiStateList.value?.map { item ->
                if (item is GroupBoardItem.GroupItem) {
                    item.copy(status = updateItem)
                } else {
                    item
                }
            }

            _entity.value = _entity.value?.copy(
                groupEntity = entity.value?.groupEntity?.copy(status = updateItem)
            )
        } else {

        }
    }

    fun onClickUpdate() {
        _event.value = entity.value?.groupEntity?.key?.let {
            GroupContentEvent.Update(
                isOwner = true,
                postKey = it
            )
        }
    }


}