package com.seven.colink.ui.group.board.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seven.colink.R
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.ui.group.board.board.GroupBoardItem
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.util.status.ApplicationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApplyRequestViewModel @Inject constructor(
    val groupRepository: GroupRepository,
    val postRepository: PostRepository,
    val userRepository: UserRepository,
    val postUseCase: GetPostUseCase,
) : ViewModel() {
    private lateinit var entity: GroupEntity

    private val _uiState = MutableLiveData<List<GroupBoardItem>>()
    val uiState: LiveData<List<GroupBoardItem>> get() = _uiState

    suspend fun setEntity(key: String) {
        entity = groupRepository.getGroupDetail(key).getOrNull() ?: return
        initViewState()
    }

    private suspend fun initViewState() {
        val postEntity = postUseCase(entity.postKey)

        val pendingUserIds = postEntity?.recruit?.flatMap { recruitInfo ->
            recruitInfo.applicationInfos?.filter {
                it.applicationStatus == ApplicationStatus.PENDING
            }?.mapNotNull { it.userId } ?: emptyList()
        } ?: emptyList()

        val userEntities = pendingUserIds.mapNotNull { userId ->
            userRepository.getUserDetails(userId).getOrNull()
        }

        val dataList = mutableListOf<GroupBoardItem>()
        dataList.add(
            GroupBoardItem.TitleItem(
                titleRes = R.string.apply_request_list,
                viewType = GroupContentViewType.TITLE
            )
        )

        postEntity?.recruit?.forEach { recruitInfo ->
            val type = recruitInfo.type ?: ""

            val usersWithType = userEntities.filter { userEntity ->
                postEntity.recruit.any { it.type == type && it.applicationInfos?.any { appInfo -> appInfo.userId == userEntity.uid } == true }
            }

            if (usersWithType.isNotEmpty()) {
                dataList.add(GroupBoardItem.SubTitleItem(title = type))
                dataList.addAll(usersWithType.map {
                    GroupBoardItem.MemberItem(
                        it,
                        isManagementButtonVisible = true
                    )
                })
            } else {
                dataList.add(GroupBoardItem.SubTitleItem(title = type))
                dataList.add(GroupBoardItem.MessageItem(message = "지원 목록이 없습니다."))
            }
        }

        _uiState.value = dataList
    }

    fun onClickApproval() {

    }

    fun onClickRefuse() {

    }

}