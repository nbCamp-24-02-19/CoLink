package com.seven.colink.ui.post.content.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.domain.usecase.RegisterApplicationInfoUseCase
import com.seven.colink.domain.usecase.RegisterPostUseCase
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    private val context: Application,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val getPostUseCase: GetPostUseCase,
    private val registerPostUseCase: RegisterPostUseCase,
    private val registerApplicationInfoUseCase: RegisterApplicationInfoUseCase,
    private val recruitRepository: RecruitRepository
) : ViewModel() {
    private lateinit var entity: Post
    private val _uiState = MutableLiveData<List<PostContentItem>>()
    val uiState: LiveData<List<PostContentItem>> get() = _uiState

    private val _dialogUiState = MutableLiveData(
        DialogUiState.init()
    )
    val dialogUiState: LiveData<DialogUiState> get() = _dialogUiState

    private val _updateButtonUiState = MutableLiveData<ContentButtonUiState>()
    val updateButtonUiState: LiveData<ContentButtonUiState> get() = _updateButtonUiState

    suspend fun setEntity(key: String) {
        entity = getPostUseCase(key) ?: return
    }

    fun initViewStateByEntity() = viewModelScope.launch {
        setUserButtonUiState(entity)
        setPostContentItems(entity.recruit)
        incrementPostViews()
    }

    private suspend fun setUserButtonUiState(post: Post) {
        _updateButtonUiState.value = when (post.authId) {
            getCurrentUser() -> ContentButtonUiState.Manager
            null -> ContentButtonUiState.Unknown
            else -> ContentButtonUiState.User
        }
    }

    private fun setPostContentItems(updatedRecruitList: List<RecruitInfo>?) =
        viewModelScope.launch {
            val items = mutableListOf<PostContentItem>()
            Log.d("확인", "${entity}")
            entity.let { currentEntity ->
                items.add(currentEntity.createPostContentItem())
                items.add(
                    PostContentItem.AdditionalInfo(
                        key = entity.key,
                        precautions = entity.precautions,
                        recruitInfo = entity.recruitInfo
                    )
                )
                items.add(
                    PostContentItem.TitleItem(
                        R.string.recruitment_status,
                        GroupContentViewType.MEMBER_ITEM
                    )
                )
                val recruitItems = createPostRecruit(updatedRecruitList)
                if (recruitItems.isEmpty()) {
                    items.add(PostContentItem.MessageItem(context.getString(R.string.no_recruitment_status)))
                } else {
                    items.addAll(recruitItems)
                }

                items.add(
                    PostContentItem.TitleItem(
                        if (currentEntity.groupType == GroupType.PROJECT) R.string.project_member_info else R.string.study_member_info,
                        GroupContentViewType.UNKNOWN
                    )
                )
                items.add(PostContentItem.SubTitleItem(R.string.project_team_member))
                items.addAll(createMember(currentEntity))

                _uiState.value = items
            }
        }

    private fun createPostRecruit(recruitList: List<RecruitInfo>?) =
        recruitList?.map { recruitInfo ->
            PostContentItem.RecruitItem(
                key = entity.key,
                recruit = recruitInfo,
                buttonUiState = updateButtonUiState.value ?: ContentButtonUiState.User
            )
        } ?: emptyList()

    private suspend fun createMember(uiState: Post): List<PostContentItem.MemberItem> {
        return uiState.memberIds.mapNotNull { memberId ->
            userRepository.getUserDetails(memberId).getOrNull()?.let {
                PostContentItem.MemberItem(key = entity.key, userInfo = it)
            }
        }
    }

    private suspend fun getCurrentUser(): String? {
        return authRepository.getCurrentUser().let {
            if (it == DataResultStatus.SUCCESS) it.message else null
        }
    }

    // 모집 분야 지원 했을 때
    suspend fun applyForProject(recruitItem: PostContentItem.RecruitItem){
        val newApplicationInfo = ApplicationInfo(
            userId = getCurrentUser(),
            applicationStatus = ApplicationStatus.PENDING,
        )
        updateRecruitList(recruitItem, newApplicationInfo)
    }

    // 지원한 회원 데이터 추가
    private suspend fun updateRecruitList(
        recruitItem: PostContentItem.RecruitItem,
        newApplicationInfo: ApplicationInfo
    ) {
        entity.recruit?.map { recruitInfo ->
            if (recruitInfo.type == recruitItem.recruit.type) {
                registerApplicationInfoUseCase.invoke(
                    ApplicationInfo(
                        key = newApplicationInfo.key,
                        recruitId = recruitItem.recruit.key,
                        userId = newApplicationInfo.userId,
                        applicationStatus = newApplicationInfo.applicationStatus,
                        applicationDate = newApplicationInfo.applicationDate
                    )
                )
            }
        }
    }

    // 이미 지원 한 회원일 때
    private suspend fun isAlreadySupported(recruitItem: PostContentItem.RecruitItem): Boolean {
        return entity.recruit?.any { recruitInfo ->
            recruitInfo.type == recruitItem.recruit.type &&
                    recruitInfo.applicationInfos?.any { it.userId == getCurrentUser() } == true
        } == true
    }

    // 지원 요청 상태가 PENDING 인 목록
    suspend fun getUserEntitiesFromRecruit(item: PostContentItem.RecruitItem): List<UserEntity> {
        return item.recruit.applicationInfos
            ?.filter { it.applicationStatus == ApplicationStatus.PENDING }
            ?.mapNotNull { it.userId?.trim() }
            ?.mapNotNull { userRepository.getUserDetails(it).getOrNull() }
            ?: emptyList()
    }

    // 지원 요청 데이터 수정
    private suspend fun updatePostRecruit(
        updatedRecruitList: List<RecruitInfo>?,
        applicationStatus: ApplicationStatus
    ): DataResultStatus {
        return entity.copy(recruit = updatedRecruitList).let { updatedEntity ->
            when (registerPostUseCase(updatedEntity)) {
                DataResultStatus.SUCCESS -> {
                    DataResultStatus.SUCCESS.apply {
                        setPostContentItems(updatedEntity.recruit)
                        message = if (applicationStatus == ApplicationStatus.APPROVE)
                            context.getString(R.string.approved_processing_completed)
                        else
                            context.getString(R.string.refusal_completed)
                    }
                }

                else -> DataResultStatus.FAIL
            }
        }
    }


    // 승인 된 사용자 멤버에 추가
    private fun updateMemberList(userEntity: UserEntity): DataResultStatus {
        return entity.let { currentEntity ->
            currentEntity.memberIds.toMutableList().apply {
                userEntity.uid?.let { add(it) }
                entity = currentEntity.copy(memberIds = this)
            }

            DataResultStatus.SUCCESS
        }
    }


    // 승인 된 회원 멤버에 추가
    suspend fun addMemberStatusApprove(
        applicationStatus: ApplicationStatus,
        userEntity: UserEntity,
        item: PostContentItem.RecruitItem
    ){
        val updatedRecruitList = updateRecruitList(applicationStatus, userEntity, item)
        if (applicationStatus == ApplicationStatus.APPROVE) {
            if (updateMemberList(userEntity) != DataResultStatus.SUCCESS) {
            }
        }
    }

    private fun updateRecruitList(
        applicationStatus: ApplicationStatus,
        userEntity: UserEntity,
        item: PostContentItem.RecruitItem
    ): List<RecruitInfo>? {
        return entity.recruit?.map { recruitInfo ->
            if (recruitInfo.type == item.recruit.type) {
                recruitInfo.copy(
                    applicationInfos = recruitInfo.applicationInfos?.map { applicationInfo ->
                        if (applicationInfo.userId == userEntity.uid) {
                            applicationInfo.copy(applicationStatus = applicationStatus)
                        } else {
                            applicationInfo
                        }
                    }.orEmpty()
                )
            } else {
                recruitInfo
            }
        }
    }

    // 조회수
    private suspend fun incrementPostViews(): DataResultStatus =
        postRepository.incrementPostViews(entity.key)


    private fun Post.createPostContentItem() = PostContentItem.Item(
        key = key,
        authId = authId,
        title = title,
        status = status,
        imageUrl = imageUrl.orEmpty(),
        groupType = groupType,
        description = description,
        tags = tags,
        registeredDate = registeredDate,
        views = views
    )

    fun createDialog(recruitItem: PostContentItem.RecruitItem) {
        _dialogUiState.value = dialogUiState.value?.copy(
            title = if (entity.groupType == GroupType.PROJECT) context.getString(R.string.project_kor) else context.getString(
                R.string.study_kor
            ),
            message = entity.title,
            recruitItem = recruitItem
        )
    }
}
