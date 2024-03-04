package com.seven.colink.ui.post.register.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.TagEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.ui.post.register.model.AddTagResult
import com.seven.colink.ui.post.register.model.ImageUiState
import com.seven.colink.ui.post.register.model.PostUiState
import com.seven.colink.ui.post.register.model.PostViewState
import com.seven.colink.util.Constants.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_GROUP_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_POST_ENTITY
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.PersonnelUtils
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val app: Application,
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val entryType: PostEntryType? by lazy {
        savedStateHandle.get<PostEntryType>(EXTRA_ENTRY_TYPE)
    }
    private lateinit var entity: PostEntity

    private val groupType: GroupType? by lazy {
        savedStateHandle.get<GroupType>(EXTRA_GROUP_TYPE)
    }

    private val _uiState: MutableLiveData<PostViewState> = MutableLiveData(PostViewState.init())
    val uiState: LiveData<PostViewState> get() = _uiState

    private val _postUiState: MutableLiveData<PostUiState> = MutableLiveData(PostUiState.init())
    val postUiState: LiveData<PostUiState> get() = _postUiState

    private val _selectedImage: MutableLiveData<ImageUiState> = MutableLiveData(ImageUiState.init())
    val selectedImage: LiveData<ImageUiState?> get() = _selectedImage

    private val _selectedPersonnelCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedPersonnelCount: LiveData<Int> get() = _selectedPersonnelCount

    init {
        try {
            entity = savedStateHandle.get<PostEntity>(EXTRA_POST_ENTITY)
                ?: throw IllegalStateException("Entity cannot be null")
        } catch (e: Exception) {
            Log.d("TAG", "${e.message}")
        }
        initViewStateByEntryType()
    }

    private fun initViewStateByEntryType() {
        _uiState.value = when (entryType) {
            PostEntryType.CREATE -> initCreateViewState()
            PostEntryType.UPDATE -> initUpdateViewState()
            else -> PostViewState.init()
        }
    }

    private fun initCreateViewState(): PostViewState {
        val isProjectSelected = groupType == GroupType.PROJECT
        val projectButtonTextColor = if (isProjectSelected) R.color.white else R.color.main_color
        val studyButtonTextColor = if (isProjectSelected) R.color.main_color else R.color.white
        val editTextContent =
            if (isProjectSelected) app.getString(R.string.input_content_project) else app.getString(
                R.string.input_content_study
            )

        return PostViewState.init().copy(
            isProjectSelected = isProjectSelected,
            projectButtonTextColor = projectButtonTextColor,
            studyButtonTextColor = studyButtonTextColor,
            editTextContent = editTextContent,
            isUpdated = false
        )
    }

    private fun initUpdateViewState(): PostViewState {
        entity.let { entity ->
            _postUiState.value = _postUiState.value?.copy(
                tagList = entity.tags?.map { TagEntity(name = it) } ?: emptyList(),
                recruitList = entity.recruit ?: emptyList(),
                totalPersonnelCount = entity.recruit?.sumOf { info -> info.maxPersonnel } ?: 0
            )

            _selectedImage.value =
                _selectedImage.value?.copy(originImage = Uri.parse(entity.imageUrl))
            _selectedPersonnelCount.value = entity.recruit?.sumOf { info -> info.maxPersonnel } ?: 0

            val isProjectSelected = entity.groupType == GroupType.PROJECT
            val projectButtonTextColor =
                if (isProjectSelected) R.color.white else R.color.main_color
            val studyButtonTextColor = if (isProjectSelected) R.color.main_color else R.color.white

            return PostViewState.init().copy(
                isProjectSelected = isProjectSelected,
                projectButtonTextColor = projectButtonTextColor,
                studyButtonTextColor = studyButtonTextColor,
                editTextTitle = entity.title,
                editTextContent = entity.description,
                isUpdated = true
            )
        }
    }

    fun addTagItem(entity: TagEntity): AddTagResult {
        val currentList = _postUiState.value?.tagList.orEmpty().toMutableList()
        return when {
            currentList.size >= LIMITED_TAG_COUNT -> AddTagResult.MaxNumberExceeded
            currentList.any { it.name == entity.name } -> AddTagResult.TagAlreadyExists
            else -> {
                _postUiState.value = _postUiState.value?.copy(tagList = currentList + entity)
                AddTagResult.Success
            }
        }
    }

    fun removeTagItem(entityKey: String?) {
        _postUiState.value = _postUiState.value?.let { state ->
            state.copy(tagList = state.tagList?.filterNot { it.key == entityKey })
        }
    }

    fun addRecruitInfo(recruitInfo: RecruitInfo) {
        val currentList = _postUiState.value?.recruitList.orEmpty().toMutableList()
        currentList.add(recruitInfo)
        _postUiState.value = _postUiState.value?.copy(recruitList = currentList)
        updateTotalPersonnelCount()
    }

    fun removeRecruitInfo(type: String?) {
        _postUiState.value = _postUiState.value?.let { state ->
            state.copy(recruitList = state.recruitList?.filterNot { it.key == key })
        }
        updateTotalPersonnelCount()
    }

    private fun updateTotalPersonnelCount() {
        val totalPersonnelCount = _postUiState.value?.recruitList?.sumOf { it.maxPersonnel ?: 0 } ?: 0
        _postUiState.value = _postUiState.value?.copy(totalPersonnelCount = totalPersonnelCount)
    }

    fun handleGalleryResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            _selectedImage.value = _selectedImage.value?.copy(newImage = selectedImageUri)
        }
    }

    fun registerPost(
        title: String,
        description: String,
        onSuccess: (Int) -> Unit,
        onError: (Exception) -> Unit
    ) = viewModelScope.launch {
        try {
            val imageUrl = selectedImage.value?.newImage?.let { uploadImage(it) }.orEmpty()
            val entity = if (entryType == PostEntryType.CREATE) {
                createPostEntity(title, description, imageUrl)
            } else {
                updatePostEntity(title, description, imageUrl)
            }

            handlePostRegistration(entity)
            onSuccess(if (entryType == PostEntryType.CREATE) R.string.post_register_success else R.string.post_update_success)
        } catch (e: Exception) {
            onError(e)
        }
    }



    private suspend fun handlePostRegistration(entity: PostEntity) {
        if (entryType == PostEntryType.CREATE) {
            postRepository.registerPost(entity)
        } else {
            postRepository.updatePost(entity.key, entity)
        }
    }

    private suspend fun createPostEntity(
        title: String,
        description: String,
        imageUrl: String?
    ): PostEntity {
        return PostEntity(
            authId = getCurrentUser(),
            title = title,
            imageUrl = imageUrl.orEmpty(),
            groupType = groupType,
            description = description,
            tags = postUiState.value?.tagList?.map { it.name },
            recruit = if (groupType == GroupType.PROJECT) postUiState.value?.recruitList
            else postUiState.value?.singleRecruitInfo?.let { listOf(it) },
            memberIds = listOf(getCurrentUser())
        )
    }

    private fun updatePostEntity(
        title: String,
        description: String,
        imageUrl: String
    ): PostEntity {
        return entity.copy(
            title = title,
            imageUrl = imageUrl.ifEmpty { entity.imageUrl },
            description = description,
            tags = postUiState.value?.tagList?.map { it.name },
            recruit = if (entity.groupType == GroupType.PROJECT) postUiState.value?.recruitList
            else postUiState.value?.singleRecruitInfo?.let { listOf(it) },
            memberIds = entity.memberIds
        )
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()


    private fun performCountOperation(operation: (Int, Int, Int) -> Pair<Int, Int>) {
        val (updateSelectedCount, updateTotalCount) = operation(
            _selectedPersonnelCount.value ?: 0,
            _postUiState.value?.totalPersonnelCount ?: 0,
            LIMITED_PEOPLE
        )
        _selectedPersonnelCount.value = updateSelectedCount

        _postUiState.value = _postUiState.value?.let { currentState ->
            val updatedRecruitInfo =
                currentState.singleRecruitInfo?.copy(maxPersonnel = updateTotalCount)
            currentState.copy(
                totalPersonnelCount = updateTotalCount,
                singleRecruitInfo = updatedRecruitInfo
            )
        }
    }

    fun incrementCount() {
        performCountOperation { selected, total, limit ->
            PersonnelUtils.incrementCount(selected, total, limit)
        }
    }

    fun decrementCount() {
        performCountOperation { selected, total, _ ->
            PersonnelUtils.decrementCount(selected, total)
        }
    }

    private suspend fun getCurrentUser(): String = authRepository.getCurrentUser().message

}