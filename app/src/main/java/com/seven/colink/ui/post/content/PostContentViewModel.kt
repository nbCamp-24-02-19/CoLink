package com.seven.colink.ui.post.content

import android.app.Application
import android.util.Log
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
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private lateinit var entity: PostEntity

    private val position: Int? by lazy { savedStateHandle.get<Int>(Constants.EXTRA_POSITION_ENTITY) }

    private val _postContentItems = MutableLiveData<List<PostContentItem>>()
    val postContentItems: LiveData<List<PostContentItem>> get() = _postContentItems

    private val _dialogUiState = MutableLiveData(
        DialogUiState.init()
    )
    val dialogUiState: LiveData<DialogUiState> get() = _dialogUiState

    init {
        try {
            entity = savedStateHandle.get<PostEntity>(Constants.EXTRA_POST_ENTITY)
                ?: throw IllegalStateException("Entity cannot be null")
            updatePostContentItems(entity.recruit)
        } catch (e: Exception) {
            Log.d("TAG", "${e.message}")
        }
    }

    private fun updatePostContentItems(updatedRecruitList: List<RecruitInfo>?) =
        viewModelScope.launch {
            val items = mutableListOf<PostContentItem>()

            items.add(createImageItem())
            items.add(createPostContentItem())
            items.add(createTitleItem(R.string.recruitment_status))
            items.addAll(createPostRecruit(updatedRecruitList))
            items.add(createTitleItem(if (entity.groupType == GroupType.PROJECT) R.string.project_member_info else R.string.study_member_info))
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
        groupType = entity.groupType,
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
                recruitInfo.type == recruitItem.recruit.type &&
                        recruitInfo.applicationInfos?.any { it.userId == getCurrentUser() } == true
            } == true) {
            return DataResultStatus.FAIL.apply {
                message = app.getString(R.string.already_supported)
            }
        }

        val newApplicationInfo = ApplicationInfo(
            userId = getCurrentUser(),
            applicationStatus = ApplicationStatus.PENDING
        )
        val updatedRecruitList = entity.recruit?.map { recruitInfo ->
            if (recruitInfo.type == recruitItem.recruit.type) {
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
                        message = app.getString(R.string.successful_support)
                    }
                }

                else -> DataResultStatus.FAIL.apply {
                    message = app.getString(R.string.support_failed_error)
                }
            }
        }
    }

    fun createDialog(recruitItem: PostContentItem.RecruitItem) {
        _dialogUiState.value = _dialogUiState.value?.copy(
            title = if (entity.groupType == GroupType.PROJECT) app.getString(R.string.project_kor) else app.getString(
                R.string.study_kor
            ),
            message = entity.title,
            recruitItem = recruitItem
        )
    }

    suspend fun getUserEntitiesFromRecruit(): List<UserEntity> {
        return entity.recruit?.flatMap { it.applicationInfos.orEmpty().map { info -> info.userId } }
            ?.distinctBy { it }
            ?.mapNotNull { it?.let { it1 -> userRepository.getUserDetails(it1).getOrNull() } }
            .orEmpty()
    }


}
