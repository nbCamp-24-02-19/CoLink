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
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.util.Constants.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_GROUP_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_POST_ENTITY
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.PersonnelUtils
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository
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

    private val _postUiState: MutableLiveData<PostUiState> = MutableLiveData(PostUiState.init())
    val postUiState: LiveData<PostUiState> get() = _postUiState

    private val _selectedImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val selectedImage: LiveData<Uri?> get() = _selectedImage

    private val _selectedPersonnelCount: MutableLiveData<Int> = MutableLiveData(0)
    val selectedPersonnelCount: LiveData<Int> get() = _selectedPersonnelCount

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
        val currentList = _postUiState.value?.tagList.orEmpty().toMutableList()
        return when {
            currentList.size >= LIMITED_TAG_COUNT -> {
                AddTagResult.MaxNumberExceeded
            }

            currentList.any { it.name == entity.name } -> {
                AddTagResult.TagAlreadyExists
            }

            else -> {
                _postUiState.value = _postUiState.value?.copy(tagList = currentList + entity)
                AddTagResult.Success
            }
        }
    }

    fun removeTagItem(entityKey: String?) {
        _postUiState.value = _postUiState.value?.let { state ->
            state.copy(tagList = state.tagList.filterNot { it.key == entityKey })
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
            state.copy(recruitList = state.recruitList.filterNot { it.type == type })
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

    private suspend fun createPostEntity(
        title: String,
        description: String,
        imageUrl: String
    ): PostEntity {
        return PostEntity(
            authId = getCurrentUser(),
            title = title,
            imageUrl = imageUrl,
            groupType = groupType,
            description = description,
            tags = postUiState.value?.tagList?.map { it.name } ?: emptyList(),
            recruit = if (groupType == GroupType.PROJECT) postUiState.value?.recruitList
                ?: emptyList()
            else postUiState.value?.singleRecruitInfo?.let { listOf(it) } ?: emptyList(),
            memberIds = getCurrentUser()?.let { listOf(it) } ?: emptyList()
        )
    }

    private suspend fun uploadImage(uri: Uri): String {
        return imageRepository.uploadImage(uri).getOrThrow().toString()
    }

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

    private suspend fun getCurrentUser(): String? {
        return authRepository.getCurrentUser().let {
            if (it == DataResultStatus.SUCCESS) it.message else null
        }
    }

}