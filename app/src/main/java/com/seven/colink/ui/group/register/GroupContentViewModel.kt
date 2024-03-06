package com.seven.colink.ui.group.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.TagEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.ui.post.register.post.model.AddTagResult
import com.seven.colink.util.Constants
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupContentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageRepository: ImageRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val key get() = savedStateHandle.get<String>(Constants.EXTRA_POST_ENTITY)
    private val entryType get() = savedStateHandle.get<PostEntryType>(Constants.EXTRA_ENTRY_TYPE)

    private val _uiState = MutableLiveData(GroupUiState.init())
    val uiState: LiveData<GroupUiState?> get() = _uiState

    private val _groupOperationResult = MutableLiveData<DataResultStatus?>()
    val groupOperationResult: LiveData<DataResultStatus?> get() = _groupOperationResult

    init {
        viewModelScope.launch {
            val entity = key?.let { groupRepository.getGroupDetail(it).getOrNull() }
            _uiState.value = uiState.value?.copy(
                title = entity?.title,
                groupType = entity?.groupType,
                description = entity?.description,
                tags = entity?.tags?.map { TagEntity(name = it) } ?: emptyList(),
                imageUrl = entity?.imageUrl,
                groupTypeUiState = if (entity?.groupType == GroupType.PROJECT) {
                    GroupTypeUiState.Project
                } else {
                    GroupTypeUiState.Study
                },
                buttonUiState = if (entryType == PostEntryType.CREATE) {
                    ContentButtonUiState.Create
                } else {
                    ContentButtonUiState.Update
                }
            )
        }
    }

    fun addTagItem(entity: TagEntity): AddTagResult {
        val list = _uiState.value?.tags.orEmpty().toMutableList()
        return when {
            list.size >= Constants.LIMITED_TAG_COUNT -> AddTagResult.MaxNumberExceeded
            list.any { it.name == entity.name } -> AddTagResult.TagAlreadyExists
            else -> {
                _uiState.value = _uiState.value?.copy(tags = list + entity)
                AddTagResult.Success
            }
        }
    }

    fun removeTagItem(key: String?) {
        _uiState.value = _uiState.value?.copy(
            tags = (_uiState.value?.tags ?: emptyList()).filter { it.key != key })
    }

    fun handleGroupEntity(
        title: String,
        description: String
    ) {
        when (entryType) {
            PostEntryType.CREATE -> onClickCreate(title, description)
            PostEntryType.UPDATE -> onClickUpdate(title, description)
            else -> Unit
        }
    }

    private fun onClickCreate(
        title: String,
        description: String
    ) = viewModelScope.launch {
        val imageUrl = uiState.value?.selectedImageUrl?.let { uploadImage(it) }.orEmpty()
        val newGroupEntity = GroupEntity(
            title = title,
            description = description,
            tags = _uiState.value?.tags?.map { it.name } ?: emptyList(),
            imageUrl = imageUrl
        )

        val result = groupRepository.registerGroup(newGroupEntity)
        _groupOperationResult.postValue(result)
    }

    private fun onClickUpdate(
        title: String,
        description: String
    ) {
        viewModelScope.launch {
            val updatedGroupEntity = GroupEntity(
                title = title,
                description = description,
                tags = _uiState.value?.tags?.map { it.name } ?: emptyList(),
                imageUrl = _uiState.value?.selectedImageUrl?.let { uploadImage(it) }
                    ?: _uiState.value?.imageUrl.orEmpty()
            )

            val result = key?.let { groupRepository.updateGroup(it, updatedGroupEntity) }
            _groupOperationResult.postValue(result)
        }
    }

    fun handleGalleryResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            _uiState.value = _uiState.value?.copy(selectedImageUrl = selectedImageUri)
        }
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()

}