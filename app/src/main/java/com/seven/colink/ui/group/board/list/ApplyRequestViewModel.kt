package com.seven.colink.ui.group.board.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.ApplicationInfoEntity
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.domain.usecase.SendNotificationUseCase
import com.seven.colink.ui.group.board.board.GroupBoardItem
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyRequestViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    val postRepository: PostRepository,
    val userRepository: UserRepository,
    val postUseCase: GetPostUseCase,
    private val recruitRepository: RecruitRepository,
    private val sendNotificationUseCase: SendNotificationUseCase,
) : ViewModel() {
    private var _entity: GroupEntity? = null
    private val entity get() = _entity!!

    private val _uiState = MutableLiveData<List<GroupBoardItem>?>()
    val uiState: LiveData<List<GroupBoardItem>?> get() = _uiState

    suspend fun setEntity(key: String) {
        _entity = groupRepository.getGroupDetail(key).getOrNull() ?: return
        initViewState()
    }

    private suspend fun initViewState() {
        val postEntity = postUseCase(entity.postKey)

        val dataList = mutableListOf<GroupBoardItem>()
        dataList.add(
            GroupBoardItem.TitleSingleItem(
                titleRes = R.string.apply_request_list,
            )
        )

        postEntity?.recruit?.forEach { recruitInfo ->
            val type = recruitInfo.type ?: ""
            val matchingApplicationInfos = recruitInfo.applicationInfos?.filter {
                it.recruitId == recruitInfo.key && it.applicationStatus == ApplicationStatus.PENDING
            } ?: emptyList()
            if (matchingApplicationInfos.isNotEmpty()) {
                dataList.add(GroupBoardItem.SubTitleItem(title = type))
                matchingApplicationInfos.forEach { applicationInfo ->
                    applicationInfo.userId?.let {
                        userRepository.getUserDetails(it).getOrNull()?.let { userEntity ->
                            dataList.add(
                                GroupBoardItem.MemberApplicationInfoItem(
                                    userEntity,
                                    applicationInfo = applicationInfo
                                )
                            )
                        }
                    }
                }
            } else {
                dataList.add(GroupBoardItem.SubTitleItem(title = type))
                dataList.add(GroupBoardItem.MessageItem(message = "지원 목록이 없습니다."))
            }
        }

        _uiState.value = dataList
    }

    fun onClickApproval(applicationInfo: ApplicationInfo?) {
        updateApplicationStatus(applicationInfo, ApplicationStatus.APPROVE)
    }

    fun onClickRefuse(applicationInfo: ApplicationInfo?) {
        updateApplicationStatus(applicationInfo, ApplicationStatus.REJECTED)
    }

    private fun updateApplicationStatus(
        applicationInfo: ApplicationInfo?,
        newStatus: ApplicationStatus
    ) {
        applicationInfo?.let {
            viewModelScope.launch {

                val updateResult = recruitRepository.registerApplicationInfo(
                    ApplicationInfoEntity(
                        key = applicationInfo.key,
                        recruitId = applicationInfo.recruitId,
                        userId = applicationInfo.userId,
                        applicationStatus = newStatus,
                        applicationDate = applicationInfo.applicationDate
                    )
                )

                if (updateResult == DataResultStatus.SUCCESS) {
                    val updatedUiState = uiState.value?.mapNotNull { item ->
                        when (item) {
                            is GroupBoardItem.MemberApplicationInfoItem -> {
                                if (item.applicationInfo == applicationInfo) {
                                    val updatedApplicationInfo = applicationInfo.copy(
                                        applicationStatus = newStatus
                                    )
                                    if (updatedApplicationInfo.applicationStatus == ApplicationStatus.PENDING) {
                                        item.copy(applicationInfo = updatedApplicationInfo)
                                    } else {
                                        null
                                    }
                                } else {
                                    item
                                }
                            }
                            else -> item
                        }
                    }?.toMutableList() ?: mutableListOf()

                    if (updatedUiState.none { it is GroupBoardItem.MemberApplicationInfoItem && it.applicationInfo?.applicationStatus == ApplicationStatus.PENDING }) {
                        updatedUiState.add(GroupBoardItem.MessageItem(message = "지원 목록이 없습니다."))
                    }

                    _uiState.postValue(updatedUiState)

                    if (newStatus == ApplicationStatus.APPROVE) {
                        _entity =
                            entity.copy(memberIds = entity.memberIds + listOf(applicationInfo.userId.orEmpty()))
                        groupRepository.updateGroupMemberIds(entity.key, entity)
                    }
                }
            }
        }
    }

    suspend fun setNotify(uid: String?) {
        postRepository.getPost(entity.postKey).getOrNull()?.let { post ->
            if (uid != null) {
                sendNotificationUseCase(post, uid)
            }
        }
    }
}