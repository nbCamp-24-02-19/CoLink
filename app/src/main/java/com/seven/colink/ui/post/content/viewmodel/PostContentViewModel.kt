package com.seven.colink.ui.post.content.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentButtonUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.util.Constants
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    private val app: Application,
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    private val entity: String = savedStateHandle.get<String>(Constants.EXTRA_POST_ENTITY)
        ?: throw IllegalStateException("Entity cannot be null")

    private val _uiState = MutableLiveData<PostEntity?>()
    val uiState: LiveData<PostEntity?> get() = _uiState

    private val _postContentItems = MutableLiveData<List<PostContentItem>>()
    val postContentItems: LiveData<List<PostContentItem>> get() = _postContentItems

    private val _dialogUiState = MutableLiveData(
        DialogUiState.init()
    )
    val dialogUiState: LiveData<DialogUiState> get() = _dialogUiState

    private val _updateButtonUiState = MutableLiveData<PostContentButtonUiState>()
    val updateButtonUiState: LiveData<PostContentButtonUiState> get() = _updateButtonUiState

    init {
        viewModelScope.launch {
            getPostByKey()
            uiState.value?.let { postEntity ->
                determineUserButtonUiState(postEntity)
                updatePostContentItems(postEntity.recruit)
                incrementPostViews()
            }
        }
    }

    private suspend fun determineUserButtonUiState(postEntity: PostEntity) {
        _updateButtonUiState.value =
            if (postEntity.authId == getCurrentUser()) PostContentButtonUiState.Writer
            else PostContentButtonUiState.Supporter
    }

    private fun updatePostContentItems(updatedRecruitList: List<RecruitInfo>?) =
        viewModelScope.launch {
            val items = mutableListOf<PostContentItem>()

            uiState.value?.let { currentUiState ->
                items.add(createImageItem(currentUiState))
                items.add(createGroupTypeItem(currentUiState))
                items.add(createPostContentItem(currentUiState))
                items.add(createTitleItem(R.string.recruitment_status))
                items.addAll(createPostRecruit(updatedRecruitList))
                items.add(createTitleItem(if (currentUiState.groupType == GroupType.PROJECT) R.string.project_member_info else R.string.study_member_info))
                items.add(createSubTitleItem(R.string.project_team_member))
                items.addAll(createMember(currentUiState))

                _postContentItems.value = items
                _uiState.value = currentUiState.copy(recruit = updatedRecruitList)
            }
        }

    private fun createGroupTypeItem(uiState: PostEntity) = PostContentItem.GroupTypeItem(
        groupType = uiState.groupType,
    )

    private fun createPostContentItem(uiState: PostEntity) = PostContentItem.Item(
        key = uiState.key,
        authId = uiState.authId,
        title = uiState.title,
        status = uiState.status,
        description = uiState.description,
        tags = uiState.tags,
        registeredDate = uiState.registeredDate,
        views = uiState.views
    )

    private fun createImageItem(uiState: PostEntity) = PostContentItem.ImageItem(
        imageUrl = uiState.imageUrl.orEmpty()
    )

    private fun createTitleItem(titleRes: Int) = PostContentItem.TitleItem(titleRes = titleRes)

    private fun createSubTitleItem(subTitleRes: Int) =
        PostContentItem.SubTitleItem(titleRes = subTitleRes)

    private fun createPostRecruit(recruitList: List<RecruitInfo>?) =
        recruitList?.map { recruitInfo ->
            PostContentItem.RecruitItem(
                recruit = recruitInfo,
                buttonUiState = updateButtonUiState.value ?: PostContentButtonUiState.Supporter
            )
        } ?: emptyList()

    private suspend fun createMember(uiState: PostEntity): List<PostContentItem.MemberItem> {
        return uiState.memberIds.mapNotNull { memberId ->
            userRepository.getUserDetails(memberId).getOrNull()?.let {
                PostContentItem.MemberItem(userInfo = it)
            }
        }
    }

    private suspend fun getCurrentUser(): String? {
        return authRepository.getCurrentUser().let {
            if (it == DataResultStatus.SUCCESS) it.message else null
        }
    }

    suspend fun applyForProject(recruitItem: PostContentItem.RecruitItem): DataResultStatus {
        if (isAlreadySupported(recruitItem)) {
            return DataResultStatus.FAIL.apply {
                message = app.getString(R.string.already_supported)
            }
        }

        val newApplicationInfo = ApplicationInfo(
            userId = getCurrentUser(),
            applicationStatus = ApplicationStatus.PENDING
        )
        val updatedRecruitList = updateRecruitList(recruitItem, newApplicationInfo)

        return updatePost(updatedRecruitList)
    }

    private suspend fun isAlreadySupported(recruitItem: PostContentItem.RecruitItem): Boolean {
        return uiState.value?.recruit?.any { recruitInfo ->
            recruitInfo.type == recruitItem.recruit.type &&
                    recruitInfo.applicationInfos?.any { it.userId == getCurrentUser() } == true
        } == true
    }

    private fun updateRecruitList(
        recruitItem: PostContentItem.RecruitItem,
        newApplicationInfo: ApplicationInfo
    ): List<RecruitInfo>? {
        return uiState.value?.recruit?.map { recruitInfo ->
            if (recruitInfo.type == recruitItem.recruit.type) {
                recruitInfo.copy(applicationInfos = (recruitInfo.applicationInfos.orEmpty() + newApplicationInfo).toList())
            } else {
                recruitInfo
            }
        }
    }

    private suspend fun updatePost(updatedRecruitList: List<RecruitInfo>?): DataResultStatus {
        return uiState.value?.copy(recruit = updatedRecruitList)?.let { updatedEntity ->
            when (postRepository.updatePost(updatedEntity.key, updatedEntity)) {
                DataResultStatus.SUCCESS -> {
                    DataResultStatus.SUCCESS.apply {
                        updatePostContentItems(updatedEntity.recruit)
                        message = app.getString(R.string.successful_support)
                    }
                }

                else -> DataResultStatus.FAIL.apply {
                    message = app.getString(R.string.failed_error)
                }
            }
        } ?: DataResultStatus.FAIL.apply {
            message = app.getString(R.string.failed_error)
        }
    }


    fun createDialog(recruitItem: PostContentItem.RecruitItem) {
        _dialogUiState.value = _dialogUiState.value?.copy(
            title = if (uiState.value?.groupType == GroupType.PROJECT) app.getString(R.string.project_kor) else app.getString(
                R.string.study_kor
            ),
            message = uiState.value?.title,
            recruitItem = recruitItem
        )
    }

    suspend fun getUserEntitiesFromRecruit(item: PostContentItem.RecruitItem): List<UserEntity> {
        return item.recruit.applicationInfos
            ?.filter { it.applicationStatus == ApplicationStatus.PENDING }
            ?.mapNotNull { it.userId?.trim() }
            ?.mapNotNull { userRepository.getUserDetails(it).getOrNull() }
            ?: emptyList()
    }

    suspend fun updateApplicationStatus(
        applicationStatus: ApplicationStatus,
        userEntity: UserEntity,
        item: PostContentItem.RecruitItem
    ): DataResultStatus {
        val updatedRecruitList = updateRecruitList(applicationStatus, userEntity, item)

        if (applicationStatus == ApplicationStatus.APPROVE) {
            if (updateMemberList(userEntity) != DataResultStatus.SUCCESS) {
                return DataResultStatus.FAIL.apply {
                    message = app.getString(R.string.failed_error)
                }
            }
        }

        return updatePost(updatedRecruitList, applicationStatus)
    }

    private fun updateRecruitList(
        applicationStatus: ApplicationStatus,
        userEntity: UserEntity,
        item: PostContentItem.RecruitItem
    ): List<RecruitInfo>? {
        return uiState.value?.recruit?.map { recruitInfo ->
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

    private fun updateMemberList(userEntity: UserEntity): DataResultStatus {
        return uiState.value?.let { currentUiState ->
            currentUiState.memberIds.toMutableList().apply {
                userEntity.uid?.let { add(it) }
                _uiState.value = currentUiState.copy(memberIds = this)
            }

            DataResultStatus.SUCCESS
        } ?: DataResultStatus.FAIL
    }

    private suspend fun updatePost(
        updatedRecruitList: List<RecruitInfo>?,
        applicationStatus: ApplicationStatus
    ): DataResultStatus {
        return uiState.value?.copy(recruit = updatedRecruitList)?.let { updatedEntity ->
            when (postRepository.updatePost(updatedEntity.key, updatedEntity)) {
                DataResultStatus.SUCCESS -> {
                    DataResultStatus.SUCCESS.apply {
                        updatePostContentItems(updatedEntity.recruit)
                        message = if (applicationStatus == ApplicationStatus.APPROVE)
                            app.getString(R.string.approved_processing_completed)
                        else
                            app.getString(R.string.refusal_completed)
                    }
                }

                else -> DataResultStatus.FAIL
            }
        } ?: DataResultStatus.FAIL.apply {
            message = app.getString(R.string.failed_error)
        }
    }

    private suspend fun getPostByKey() {
        _uiState.value = postRepository.getPost(entity).getOrNull()
    }

    private suspend fun incrementPostViews(): DataResultStatus =
        postRepository.incrementPostViews(entity)


}
