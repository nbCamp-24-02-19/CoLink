package com.seven.colink.ui.post.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.util.Constants
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private var entity: PostEntity

    private val position: Int? by lazy { savedStateHandle.get<Int>(Constants.EXTRA_POSITION_ENTITY) }
    private val groupType: GroupType? by lazy { savedStateHandle.get<GroupType>(Constants.EXTRA_GROUP_TYPE) }

    private val _postContentItems = MutableLiveData<List<PostContentItem>>()
    val postContentItems: LiveData<List<PostContentItem>> get() = _postContentItems

    init {
        entity = savedStateHandle.get<PostEntity>(Constants.EXTRA_POST_ENTITY)
            ?: throw IllegalStateException("Entity cannot be null")
        updatePostContentItems(entity.recruit)
    }

    private fun updatePostContentItems(updatedRecruitList: List<RecruitInfo>?) =
        viewModelScope.launch {
            val items = mutableListOf<PostContentItem>()

            items.add(createImageItem()) // 이미지
            items.add(createPostContentItem()) // 포스트 내용
            items.add(createTitleItem(R.string.recruitment_status))
            items.addAll(createPostRecruit(updatedRecruitList))
            items.add(createTitleItem(if (groupType == GroupType.PROJECT) R.string.project_member_info else R.string.study_member_info))
            items.add(createSubTitleItem(R.string.project_team_member))
            items.addAll(createMember())

            _postContentItems.value = items
            entity = entity.copy(recruit = updatedRecruitList)
        }

    private fun createPostContentItem() = PostContentItem.Item(
        key = entity.key,
        authId = entity.authId,
        title = entity.title,
        status = entity.status,
        groupType = groupType,
        description = entity.description,
        tags = entity.tags,
        registeredDate = entity.registeredDate,
        views = entity.views
    )

    private fun createImageItem() = PostContentItem.ImageItem(
        imageUrl = entity.imageUrl.orEmpty()
    )

    private fun createTitleItem(titleRes: Int) = PostContentItem.TitleItem(titleRes = titleRes)

    private fun createSubTitleItem(subTitleRes: Int) =
        PostContentItem.SubTitleItem(titleRes = subTitleRes)

    private suspend fun createPostRecruit(recruitList: List<RecruitInfo>?) =
        recruitList?.map { recruitInfo ->
            val buttonUiState =
                if (entity.authId == getCurrentUser()) PostContentButtonUiState.Writer else PostContentButtonUiState.Supporter
            PostContentItem.RecruitItem(recruit = recruitInfo, buttonUiState = buttonUiState)
        } ?: emptyList()

    private suspend fun createMember(): List<PostContentItem.MemberItem> {
        return entity.memberIds.mapNotNull { memberId ->
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

    suspend fun applyForProject(
        recruitItem: PostContentItem.RecruitItem
    ): DataResultStatus {
        if (entity.recruit?.any { recruitInfo ->
                recruitInfo.key == recruitItem.recruit.key &&
                        recruitInfo.applicationInfos?.any { it.userId == getCurrentUser() } == true
            } == true) {
            return DataResultStatus.FAIL.apply { message = "Already applied to this post." }
        }

        val newApplicationInfo = ApplicationInfo(
            userId = getCurrentUser(),
            applicationStatus = ApplicationStatus.PENDING
        )
        val updatedRecruitList = entity.recruit?.map { recruitInfo ->
            if (recruitInfo.key == recruitItem.recruit.key) {
                recruitInfo.copy(applicationInfos = (recruitInfo.applicationInfos.orEmpty() + newApplicationInfo).toList())
            } else {
                recruitInfo
            }
        }

        return entity.copy(recruit = updatedRecruitList).let {
            when (postRepository.updatePost(it.key, it)) {
                DataResultStatus.SUCCESS -> {
                    DataResultStatus.SUCCESS.apply {
                        updatePostContentItems(it.recruit)
                        message = "Successfully applied to the post."
                    }
                }

                else -> DataResultStatus.FAIL.apply { message = "Unknown error occurred." }
            }
        }
    }
}
