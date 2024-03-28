package com.seven.colink.ui.group.board.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GroupBoardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val postUseCase: GetPostUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _uiStateList = MutableLiveData<List<GroupBoardItem>?>()
    val uiStateList: LiveData<List<GroupBoardItem>?> get() = _uiStateList

    private val _entity = MutableLiveData(GroupBoardUiState.init())
    val entity: LiveData<GroupBoardUiState?> get() = _entity

    private val _result = MutableLiveData<DataResultStatus>()
    val result : LiveData<DataResultStatus> get() = _result
    lateinit var productKey : String

    private suspend fun getProduct(postKey: String) : ProductEntity? {
        return productRepository.getProduct(postKey).getOrNull()
    }

    private fun compareKey(key: String)  {
        viewModelScope.launch {
            val compareItem = getProduct(key)
            if (key == compareItem?.postKey) {
                _result.value = DataResultStatus.SUCCESS
            }else {
                _result.value = DataResultStatus.FAIL
            }
        }
    }

    fun setEntity(key: String) = viewModelScope.launch {
        val groupEntity = key.let { groupRepository.getGroupDetail(it).getOrNull() }
        _entity.value = entity.value?.copy(groupEntity = groupEntity)
        entity.value?.let {
            checkCurrentUser(it.groupEntity)
            groupContentItems()
        }
        compareKey(key)
        productKey = productRepository.getProduct(key).getOrNull()?.key.toString()
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
                    isOwner = entity.authId == getCurrentUser(),
                    projectStartDate = entity.projectStartDate,
                    projectEndDate = entity.projectEndDate
                )
            )
            items.add(
                GroupBoardItem.GroupOptionItem(
                    key = entity.key,
                    precautions = entity.precautions,
                    startDate = entity.startDate,
                    endDate = entity.endDate
                )
            )
            items.add(
                GroupBoardItem.TitleItem(
                    titleRes = R.string.group_post_title,
                    viewType = GroupContentViewType.POST_ITEM,
                    buttonUiState = if (entity.authId == getCurrentUser()) ContentButtonUiState.Manager
                    else ContentButtonUiState.User
                )
            )
            postEntity?.let { items.add(GroupBoardItem.PostItem(it)) }
            items.add(
                GroupBoardItem.TitleItem(
                    titleRes = R.string.project_member_info,
                    viewType = GroupContentViewType.MEMBER_ITEM,
                    buttonUiState = if (entity.authId == getCurrentUser()) ContentButtonUiState.Manager
                    else ContentButtonUiState.User
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

                memberItems.add(GroupBoardItem.MemberItem(userEntity))
            }
        }

        return memberItems
    }

    fun onChangedStatus(status: ProjectStatus) = viewModelScope.launch {
        val updatedList = uiStateList.value?.map { uiStateValue ->
            when (uiStateValue) {
                is GroupBoardItem.GroupItem -> {
                    val (key, dateField) = when (status) {
                        ProjectStatus.RECRUIT -> uiStateValue.key to "projectStartDate"
                        else -> uiStateValue.key to "projectEndDate"
                    }

                    val result = groupRepository.updateGroupStatus(
                        key ?: "",
                        getNextStatus(status),
                        mapOf(dateField to LocalDateTime.now().convertLocalDateTime())
                    )

                    if (result == DataResultStatus.SUCCESS) {
                        uiStateValue.copy(
                            status = getNextStatus(status),
                            projectStartDate = if (status == ProjectStatus.RECRUIT) LocalDateTime.now().convertLocalDateTime() else uiStateValue.startDate,
                            projectEndDate = if (status != ProjectStatus.RECRUIT) LocalDateTime.now().convertLocalDateTime() else uiStateValue.endDate
                        )
                    } else {
                        uiStateValue
                    }
                }
                else -> uiStateValue
            }
        }

        _uiStateList.value = updatedList
    }


    private fun getNextStatus(currentStatus: ProjectStatus): ProjectStatus {
        return when (currentStatus) {
            ProjectStatus.RECRUIT -> {
                ProjectStatus.START
            }

            ProjectStatus.START -> {
                ProjectStatus.END
            }

            else -> currentStatus
        }
    }

}