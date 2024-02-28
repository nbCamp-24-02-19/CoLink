package com.seven.colink.ui.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.util.Constants.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_GROUP_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_POST_ENTITY
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val entryType: PostEntryType? by lazy {
        savedStateHandle.get<PostEntryType>(EXTRA_ENTRY_TYPE)
    }
    private val entity: PostEntity? by lazy {
        savedStateHandle.get<PostEntity>(EXTRA_POST_ENTITY)
    }

    private val groupType: GroupType? by lazy {
        savedStateHandle.get<GroupType>(EXTRA_GROUP_TYPE)
    }

    private val _uiState: MutableLiveData<PostViewState> = MutableLiveData(PostViewState.init())
    val uiState: LiveData<PostViewState> get() = _uiState

    private val _tagUiState: MutableLiveData<TagUiState> = MutableLiveData(TagUiState.init())
    val tagUiState: LiveData<TagUiState> get() = _tagUiState

    private val _recruitUiState: MutableLiveData<RecruitUiState> =
        MutableLiveData(RecruitUiState.init())
    val recruitUiState: LiveData<RecruitUiState> get() = _recruitUiState

    private val _selectedImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val selectedImage: LiveData<Uri?> get() = _selectedImage

    init {
        initViewState()
    }

    private fun initViewState() {
        _uiState.value = when (groupType) {
            GroupType.PROJECT -> PostViewState.init().copy(
                isProjectSelected = true,
                projectButtonTextColor = R.color.white,
                studyButtonTextColor = R.color.main_color,
                editTextContent = R.string.input_content_project
            )

            GroupType.STUDY -> PostViewState.init().copy(
                isProjectSelected = false,
                projectButtonTextColor = R.color.main_color,
                studyButtonTextColor = R.color.white,
                editTextContent = R.string.input_content_study
            )

            else -> PostViewState.init()
        }
    }

    fun addTagItem(entity: TagEntity): AddTagResult {
        val currentList = _tagUiState.value?.list.orEmpty().toMutableList()
        return when {
            currentList.size >= LIMITED_TAG_COUNT -> {
                AddTagResult.MaxNumberExceeded
            }

            currentList.any { it.name == entity.name } -> {
                AddTagResult.TagAlreadyExists
            }

            else -> {
                currentList.add(entity)
                _tagUiState.value = _tagUiState.value?.copy(list = currentList)
                AddTagResult.Success
            }
        }
    }

    fun removeTagItem(entityKey: String?) {
        _tagUiState.value = _tagUiState.value?.let { state ->
            state.copy(list = state.list.filterNot { it.key == entityKey })
        }
    }

    fun addRecruitInfo(recruitInfo: RecruitInfo) {
        val currentList = _recruitUiState.value?.list.orEmpty().toMutableList()
        currentList.add(recruitInfo)
        _recruitUiState.value = _recruitUiState.value?.copy(list = currentList)
        updateTotalPersonnelCount()
    }

    fun removeRecruitInfo(key: String) {
        _recruitUiState.value = _recruitUiState.value?.let { state ->
            state.copy(list = state.list.filterNot { it.key == key })
        }
        updateTotalPersonnelCount()
    }

    private fun updateTotalPersonnelCount() {
        val totalPersonnelCount = _recruitUiState.value?.list?.sumOf { it.maxPersonnel } ?: 0
        _recruitUiState.value =
            _recruitUiState.value?.copy(totalPersonnelCount = totalPersonnelCount)
    }

    fun handleGalleryResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            _selectedImage.value = selectedImageUri
        }
    }

    fun registerPost(
        title: String, description: String,
        onSuccess: () -> Unit, onError: (Exception) -> Unit
    ) = viewModelScope.launch {
        try {
            val imageUrl = selectedImage.value?.let { uploadImage(it) }.orEmpty()
            val entity = createPostEntity(title, description, imageUrl)
            postRepository.registerPost(entity)
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun createPostEntity(
        title: String,
        description: String,
        imageUrl: String
    ): PostEntity {
        return PostEntity(
            authId = "user123",
            title = title,
            imageUrl = imageUrl,
            groupType = groupType,
            description = description,
            tags = tagUiState.value?.list?.map { it.name } ?: emptyList(),
            recruit = recruitUiState.value?.list ?: emptyList(),
        )
    }

    private suspend fun uploadImage(uri: Uri): String {
        return imageRepository.uploadImage(uri).getOrThrow().toString()
    }

}