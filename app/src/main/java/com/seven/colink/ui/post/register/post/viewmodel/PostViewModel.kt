package com.seven.colink.ui.post.register.post.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import com.seven.colink.ui.post.register.post.PostEvent
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.ui.post.register.post.model.PostUiState
import com.seven.colink.ui.post.register.post.model.TagEvent
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _uiState: MutableLiveData<PostUiState> = MutableLiveData(PostUiState.init())
    val uiState: LiveData<PostUiState> get() = _uiState

    private val _errorUiState: MutableLiveData<PostErrorUiState> =
        MutableLiveData(PostErrorUiState.init())
    val errorUiState: LiveData<PostErrorUiState> get() = _errorUiState

    private val _event: MutableLiveData<PostEvent> = MutableLiveData()
    val event: LiveData<PostEvent> get() = _event

    private val _selectedCount = MutableLiveData<Int>().apply { value = 0 }
    val selectedCount: LiveData<Int> get() = _selectedCount

    private val _complete = MutableSharedFlow<String>()
    val complete: SharedFlow<String> = _complete

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
        setCreateView()
    }

    fun setViewByEntity() {
        setUpdateView()
    }

    private fun setCreateView() {
        _uiState.value = _uiState.value?.copy(
            groupType = groupType,
            descriptionMessage = if (groupType == GroupType.PROJECT) context.getString(R.string.input_content_project) else context.getString(
                R.string.input_content_study
            )
        )
    }

    private fun setUpdateView() {
        entity.let { entity ->
            _uiState.value = _uiState.value?.copy(
                title = entity.title,
                imageUrl = entity.imageUrl,
                groupType = entity.groupType ?: GroupType.UNKNOWN,
                description = entity.description,
                descriptionMessage = if (entity.groupType == GroupType.PROJECT) context.getString(R.string.input_content_project) else context.getString(
                    R.string.input_content_study
                ),
                tags = entity.tags,
                precautions = entity.precautions,
                recruitInfo = entity.recruitInfo,
                recruit = entity.recruit,
                registeredDate = entity.registeredDate,
                view = entity.views,
                memberIds = entity.memberIds
            )

        }
    }

    fun addTagItem(tag: String): TagEvent {
        val currentTags = _uiState.value?.tags.orEmpty()
        return when {
            currentTags.size >= LIMITED_TAG_COUNT -> TagEvent.MaxNumberExceeded
            currentTags.any { it == tag } -> TagEvent.TagAlreadyExists
            else -> {
                _uiState.value = _uiState.value?.copy(
                    tags = currentTags + tag
                )
                TagEvent.Success
            }
        }
    }

    fun removeTagItem(tag: String?) {
        _uiState.value = _uiState.value?.let { state ->
            state.copy(
                tags = state.tags?.filterNot { it == tag }
            )
        }
    }

    fun addRecruitInfo(entity: RecruitInfo) {
        val recruits = uiState.value?.recruit.orEmpty().toMutableList()
        recruits.add(entity)
        _uiState.value = _uiState.value?.copy(
            recruit = recruits
        )
    }

    fun removeRecruitInfo(type: String?) {
        _uiState.value = _uiState.value?.let { state ->
            state.copy(
                recruit = state.recruit?.toMutableList()?.apply {
                    removeAll { it.type == type }
                }
            )
        }
    }

    fun createPost(
        title: String,
        description: String,
        precautions: String,
        recruitInfo: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) = viewModelScope.launch {
        try {
            _uiState.value = _uiState.value?.copy(
                title = title,
                description = description,
                precautions = precautions,
                recruitInfo = recruitInfo
            )

            val entity = if (entryType == PostEntryType.CREATE) {
                getCreatePostEntity(title, description, precautions, recruitInfo)
            } else {
                getUpdatePostEntity(title, description, precautions, recruitInfo)
            }

            if (entryType == PostEntryType.CREATE) {
                registerPostUseCase(entity)
                try {
                    groupRepository.registerGroup(entity.convertGroupEntity())
                    onSuccess(context.getString(R.string.post_register_success))
                    _complete.emit(entity.key)
                    Log.d("1111", "success")
                } catch (groupException: Exception) {
                    onError(groupException)
                    Log.d("1111", "error= $groupException")
                }
            } else {

                registerPostUseCase(entity)
                onSuccess(context.getString(R.string.post_update_success))
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    private suspend fun getCreatePostEntity(
        title: String,
        description: String,
        precautions: String,
        recruitInfo: String
    ): Post {
        val currentState = uiState.value

        return Post(
            authId = getCurrentUser(),
            title = title,
            imageUrl = currentState?.selectedImageUrl?.let { uploadImage(it) },
            groupType = groupType,
            description = description,
            tags = currentState?.tags,
            precautions = precautions,
            recruitInfo = recruitInfo,
            recruit = currentState?.recruit,
            memberIds = listOf(getCurrentUser())
        )
    }

    private suspend fun getUpdatePostEntity(
        title: String,
        description: String,
        precautions: String,
        recruitInfo: String
    ): Post {
        val currentState = uiState.value
        val imageUrl =
            currentState?.selectedImageUrl?.let { uploadImage(it) } ?: currentState?.imageUrl

        return entity.copy(
            title = title,
            imageUrl = imageUrl,
            description = description,
            tags = currentState?.tags,
            precautions = precautions,
            recruitInfo = recruitInfo,
            recruit = currentState?.recruit,
            memberIds = entity.memberIds
        )
    }

    fun incrementCount() {
        val currentCount = _selectedCount.value ?: 0
        _selectedCount.value = currentCount + 1
        updateRecruitBasedOnCount()
    }

    fun decrementCount() {
        val currentCount = _selectedCount.value ?: 0
        _selectedCount.value = if (currentCount > 0) currentCount - 1 else 0
        updateRecruitBasedOnCount()
    }

    private fun updateRecruitBasedOnCount() {
        val newCount = _selectedCount.value ?: 0
        val updatedRecruit = _uiState.value?.recruit?.map {
            it.copy(maxPersonnel = newCount)
        }
        updateRecruit(updatedRecruit)
    }

    fun updateRecruit(updatedRecruit: List<RecruitInfo>?) {
        _uiState.value = _uiState.value?.copy(recruit = updatedRecruit)
    }


    private suspend fun getCurrentUser(): String =
        authRepository.getCurrentUser().message

    private suspend fun Post.convertGroupEntity() = GroupEntity(
        key = key,
        postKey = key,
        authId = authId,
        teamName = runCatching {
            val userEntity = authRepository.getCurrentUser()
            "${userEntity.name}님의 팀"
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

    fun handleGalleryResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            _uiState.value = _uiState.value?.copy(
                selectedImageUrl = selectedImageUri
            )
        }
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()

    fun onClickRecruit() {
        _event.value = PostEvent.DialogEvent(
            uiState.value?.recruit
        )
    }

    fun checkValid(title: String, description: String) {
        _errorUiState.value = errorUiState.value?.copy(
            message = when {
                title.isBlank() -> PostErrorMessage.TITLE_BLANK
                description.isBlank() -> PostErrorMessage.DESCRIPTION_BLANK
                else -> PostErrorMessage.PASS
            }
        )

    }

}