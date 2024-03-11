package com.seven.colink.ui.post.register.post.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.domain.usecase.RegisterPostUseCase
import com.seven.colink.ui.post.register.post.PostErrorMessage
import com.seven.colink.ui.post.register.post.PostErrorUiState
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.ui.post.register.post.model.PostListItem
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val context: Application,
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    private val getPostUseCase: GetPostUseCase,
    private val registerPostUseCase: RegisterPostUseCase,
) : ViewModel() {
    private lateinit var entryType: PostEntryType
    private lateinit var entity: Post
    private lateinit var groupType: GroupType

    private val _uiState: MutableLiveData<List<PostListItem>> = MutableLiveData()
    val uiState: LiveData<List<PostListItem>> get() = _uiState
    private val _errorUiState: MutableLiveData<PostErrorUiState> =
        MutableLiveData(PostErrorUiState.init())
    val errorUiState: LiveData<PostErrorUiState> get() = _errorUiState

    private val _complete = MutableSharedFlow<String>()
    val complete = _complete.asSharedFlow()

    fun setEntryType(type: PostEntryType) {
        entryType = type
    }

    fun setGroupType(type: GroupType) {
        groupType = type
    }

    suspend fun setEntity(key: String) {
        entity = getPostUseCase(key) ?: Post()
    }

    fun setViewByGroupType() {
        setPostCreateItem()
    }

    fun setViewByEntity() {
        setPostUpdateItem()
    }

    private fun setPostUpdateItem() {
        val items = mutableListOf<PostListItem>()
        items.add(
            PostListItem.PostItem(
                key = entity.key,
                title = entity.title,
                imageUrl = entity.imageUrl,
                groupType = entity.groupType ?: GroupType.UNKNOWN,
                selectedImageUrl = null,
                description = entity.description,
                descriptionMessage = if (entity.groupType == GroupType.PROJECT) context.getString(R.string.input_content_project) else context.getString(
                    R.string.input_content_study
                ),
                tags = entity.tags,
                registeredDate = entity.registeredDate,
                view = entity.views,
                memberIds = entity.memberIds
            )
        )
        items.add(
            PostListItem.PostOptionItem(
                key = entity.key,
                precautions = entity.precautions,
                recruitInfo = entity.recruitInfo
            )
        )
        items.add(
            PostListItem.TitleItem(
                R.string.people_recruited,
                R.string.limited_people,
            )
        )
        items.add(
            PostListItem.RecruitItem(
                key = entity.key,
                recruit = entity.recruit,
                groupType = entity.groupType ?: GroupType.PROJECT,
                selectedCount = 0
            )
        )
        items.add(
            PostListItem.ButtonItem(
                text = R.string.bt_complete
            )
        )

        _uiState.postValue(items)
    }

    private fun setPostCreateItem() {
        val items = mutableListOf<PostListItem>()
        items.add(
            PostListItem.PostItem(
                title = null,
                key = null,
                imageUrl = null,
                groupType = groupType,
                selectedImageUrl = null,
                description = null,
                descriptionMessage = if (groupType == GroupType.PROJECT) context.getString(R.string.input_content_project) else context.getString(
                    R.string.input_content_study
                ),
                tags = null,
                registeredDate = null,
                view = 0,
                memberIds = emptyList()
            )
        )
        items.add(
            PostListItem.PostOptionItem(
                key = null,
                precautions = null,
                recruitInfo = null
            )
        )
        items.add(
            PostListItem.TitleItem(
                R.string.people_recruited,
                R.string.limited_people,
            )
        )
        items.add(
            PostListItem.RecruitItem(
                key = null,
                recruit = null,
                groupType = groupType,
                selectedCount = 0
            )
        )
        items.add(
            PostListItem.ButtonItem(
                text = R.string.bt_complete
            )
        )

        _uiState.postValue(items)
    }


    fun checkValidAddTag(tag: String) {
        _errorUiState.value = errorUiState.value?.copy(tag = getValidAddTag(tag))
    }

    private fun getValidAddTag(tag: String): PostErrorMessage {
        val list = when (val uiStateValue = uiState.value?.firstOrNull()) {
            is PostListItem.PostItem -> uiStateValue.tags.orEmpty()
            else -> emptyList()
        }
        return when {
            list.size >= LIMITED_TAG_COUNT -> PostErrorMessage.TAG_MAX_COUNT
            list.any { it == tag } -> PostErrorMessage.TAG_ALREADY_EXIST
            else -> {
                _uiState.value = uiState.value?.map { uiStateValue ->
                    when (uiStateValue) {
                        is PostListItem.PostItem -> uiStateValue.copy(tags = list + tag)
                        else -> uiStateValue
                    }
                }
                PostErrorMessage.PASS
            }
        }
    }

    fun removeTagItem(tag: String?) {
        _uiState.value = uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.PostItem -> uiStateValue.copy(
                    tags = uiStateValue.tags?.filter { it != tag } ?: emptyList()
                )

                else -> uiStateValue
            }
        }
    }

    private suspend fun getCurrentUser(): String =
        authRepository.getCurrentUser().message

    private suspend fun Post.convertGroupEntity() = GroupEntity(
        key = key,
        postKey = key,
        authId = authId,
        teamName = runCatching {
            "${getCurrentUser()}님의 팀"
        }.getOrElse { "" },
        title = title,
        imageUrl = imageUrl,
        status = status,
        groupType = groupType ?: GroupType.UNKNOWN,
        description = description,
        tags = tags,
        memberIds = memberIds,
        registeredDate = registeredDate
    )

    fun setImageResult(data: Intent?) {
        _uiState.value = uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.PostItem -> uiStateValue.copy(selectedImageUrl = data?.data)
                else -> uiStateValue
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()

    fun arePostListItemFieldsValid() {
        val postListItems = uiState.value

        val postItemsValid = postListItems?.filterIsInstance<PostListItem.PostItem>()
            ?.all { it.title != null && it.description != null } ?: false

        val postOptionItemsValid = postListItems?.filterIsInstance<PostListItem.PostOptionItem>()
            ?.all { it.precautions != null && it.recruitInfo != null } ?: false

        if (postItemsValid && postOptionItemsValid) {
            _errorUiState.value = errorUiState.value?.copy(message = PostErrorMessage.PASS)
        } else {
            postListItems?.forEach { postListItem ->
                when (postListItem) {
                    is PostListItem.PostItem -> {
                        checkPostItem(postListItem)
                    }
                    is PostListItem.PostOptionItem -> {
                        checkPostOptionItem(postListItem)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun checkPostItem(postItem: PostListItem.PostItem) {
        when {
            postItem.description != null -> {
                _errorUiState.value = errorUiState.value?.copy(message = PostErrorMessage.DESCRIPTION_BLANK)
            }
        }
    }

    private fun checkPostOptionItem(postOptionItem: PostListItem.PostOptionItem) {
        when {
            postOptionItem.precautions == null -> {
                _errorUiState.value = errorUiState.value?.copy(message = PostErrorMessage.PRECAUTIONS_BLANK)
            }
            postOptionItem.recruitInfo == null -> {
                _errorUiState.value = errorUiState.value?.copy(message = PostErrorMessage.RECRUIT_INFO_BLANK)
            }
        }
    }

    fun addRecruitInfo(entity: RecruitInfo) {
        _uiState.value = uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> {
                    val recruits = uiStateValue.recruit?.toMutableList() ?: mutableListOf()
                    recruits.add(entity)
                    uiStateValue.copy(recruit = recruits)
                }

                else -> uiStateValue
            }
        }
    }


    fun removeRecruitInfo(type: String?) {
        _uiState.value = uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> uiStateValue.copy(
                    recruit = uiStateValue.recruit?.toMutableList()?.apply {
                        removeAll { it.type == type }
                    }
                )

                else -> uiStateValue
            }
        }
    }

    fun createPost(
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) = viewModelScope.launch {
        try {
            val entity = if (entryType == PostEntryType.CREATE) {
                getCreatePostEntity()
            } else {
                getUpdatePostEntity()
            }

//            if (entryType == PostEntryType.CREATE) {
//                try {
//                    registerPostUseCase(entity)
//                    groupRepository.registerGroup(entity.convertGroupEntity())
//                    onSuccess(context.getString(R.string.post_register_success))
//                    _complete.emit(entity.key)
//                } catch (groupException: Exception) {
//                    onError(groupException)
//                }
//            } else {
//                try {
//                    registerPostUseCase(entity)
//                    onSuccess(context.getString(R.string.post_update_success))
//                } catch (e: Exception) {
//                    onError(e)
//                }
//            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    private suspend fun getCreatePostEntity(): Post {
        val postItem = _uiState.value?.find { it is PostListItem.PostItem } as? PostListItem.PostItem

        return Post(
            authId = getCurrentUser(),
            title = postItem?.title,
            imageUrl = postItem?.selectedImageUrl?.let { uploadImage(it) },
            groupType = groupType,
            description = postItem?.description,
            tags = postItem?.tags,
            precautions = _uiState.value?.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.precautions },
            recruitInfo = _uiState.value?.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.recruitInfo },
            recruit = (_uiState.value?.find { it is PostListItem.RecruitItem }
                    as? PostListItem.RecruitItem)?.recruit,
            memberIds = listOf(getCurrentUser())
        )
    }

    private suspend fun getUpdatePostEntity(): Post {
        val postItem = _uiState.value?.find { it is PostListItem.PostItem } as? PostListItem.PostItem
        val imageUrl = postItem?.selectedImageUrl?.let { uploadImage(it) } ?: postItem?.imageUrl

        return entity.copy(
            title = postItem?.title,
            imageUrl = imageUrl,
            description = postItem?.description,
            tags = postItem?.tags,
            precautions = _uiState.value?.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.precautions },
            recruitInfo = _uiState.value?.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.recruitInfo },
            recruit = (_uiState.value?.find { it is PostListItem.RecruitItem }
                    as? PostListItem.RecruitItem)?.recruit,
            memberIds = entity.memberIds
        )
    }

    fun incrementCount() {
        _uiState.value = uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> {
                    val currentCount = uiStateValue.selectedCount ?: 0
                    uiStateValue.copy(selectedCount = currentCount + 1)
                }

                else -> uiStateValue
            }

        }
        updateRecruitBasedOnCount()
    }

    fun decrementCount() {
        _uiState.value = uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> {
                    val currentCount = uiStateValue.selectedCount ?: 0
                    uiStateValue.copy(selectedCount = if (currentCount > 0) currentCount - 1 else 0)
                }

                else -> uiStateValue
            }

        }
        updateRecruitBasedOnCount()
    }

    private fun updateRecruitBasedOnCount() {
        _uiState.value = _uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> {
                    val selectedCount = uiStateValue.selectedCount ?: 0
                    val updatedRecruit = uiStateValue.recruit?.toMutableList() ?: mutableListOf()

                    val existingRecruitIndex = updatedRecruit.indexOfFirst { it.type == "" }

                    if (existingRecruitIndex != -1) {
                        updatedRecruit[existingRecruitIndex] =
                            updatedRecruit[existingRecruitIndex].copy(maxPersonnel = selectedCount)
                    } else {
                        val newRecruitInfo = RecruitInfo(type = "", maxPersonnel = selectedCount)
                        updatedRecruit.add(newRecruitInfo)
                    }

                    uiStateValue.copy(selectedCount = selectedCount, recruit = updatedRecruit)
                }

                else -> uiStateValue
            }
        }

        val updatedRecruit = _uiState.value?.find { it is PostListItem.RecruitItem }?.let {
            val recruitItem = it as? PostListItem.RecruitItem
            recruitItem?.recruit
        }
        updateRecruit(updatedRecruit)
    }

    private fun updateRecruit(updatedRecruit: List<RecruitInfo>?) {
        _uiState.value = _uiState.value?.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> uiStateValue.copy(recruit = updatedRecruit)
                else -> uiStateValue
            }
        }
    }

    fun updatePostItemText(position: Int, title: String, description: String) {
        _uiState.value = _uiState.value?.mapIndexed { index, uiStateValue ->
            when (uiStateValue) {
                is PostListItem.PostItem -> {
                    if (index == position) {
                        uiStateValue.copy(title = title, description = description)
                    } else {
                        uiStateValue
                    }
                }

                else -> uiStateValue
            }
        }
    }

fun updatePostOprionItemText(position: Int, title: String, description: String) {
    _uiState.value = _uiState.value?.mapIndexed { index, uiStateValue ->
        when (uiStateValue) {
            is PostListItem.PostOptionItem -> {
                if (index == position) {
                    uiStateValue.copy(precautions = title, recruitInfo = description)
                } else {
                    uiStateValue
                }
            }

            else -> uiStateValue
        }
    }
}
}