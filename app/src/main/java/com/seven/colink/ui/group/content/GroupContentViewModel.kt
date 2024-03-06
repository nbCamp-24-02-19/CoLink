package com.seven.colink.ui.group.content

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val imageRepository: ImageRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private lateinit var entryType: PostEntryType
    private val _uiState = MutableLiveData<GroupContentItem.GroupContent>()
    val uiState: LiveData<GroupContentItem.GroupContent?> get() = _uiState

    private val _groupOperationResult = MutableLiveData<DataResultStatus?>()
    val groupOperationResult: LiveData<DataResultStatus?> get() = _groupOperationResult

    fun setEntryType(type: PostEntryType) {
        entryType = type
    }

    suspend fun setEntity(key: String) {
        viewModelScope.launch {
            val entity = key.let { groupRepository.getGroupDetail(it).getOrNull() }
            _uiState.value = entity?.let {
                GroupContentItem.GroupContent(
                    key = it.key,
                    teamName = it.teamName,
                    groupType = it.groupType,
                    description = it.description,
                    tags = it.tags?.map { TagEntity(name = it) } ?: emptyList(),
                    imageUrl = it.imageUrl,
                    selectedImageUrl = null,
                    groupTypeUiState = if (it.groupType == GroupType.PROJECT) GroupTypeUiState.Project else GroupTypeUiState.Study,
                    buttonUiState = if (entryType == PostEntryType.CREATE) ContentButtonUiState.Create else ContentButtonUiState.Update
                )
            }
        }
    }

    fun addTagItem(entity: TagEntity): AddTagResult {
        val currentUiState = _uiState.value?.copy()
        val list = currentUiState?.tags.orEmpty().toMutableList()

        return when {
            list.size >= Constants.LIMITED_TAG_COUNT -> AddTagResult.MaxNumberExceeded
            list.any { it.name == entity.name } -> AddTagResult.TagAlreadyExists
            else -> {
                _uiState.value = currentUiState?.copy(tags = list + entity)
                AddTagResult.Success
            }
        }
    }

    fun removeTagItem(key: String?) {
        _uiState.value = _uiState.value?.copy(
            tags = _uiState.value?.tags?.filter { it.key != key } ?: emptyList()
        )
    }

    private fun onClickCreate(teamName: String, description: String) = viewModelScope.launch {
        val imageUrl = _uiState.value?.selectedImageUrl?.let { uploadImage(it) }.orEmpty()
        val newGroupEntity = createGroupEntity(teamName, description, imageUrl)

        _groupOperationResult.postValue(groupRepository.registerGroup(newGroupEntity))
    }

    private fun onClickUpdate(teamName: String, description: String) = viewModelScope.launch {
        val updatedGroupEntity = createGroupEntity(
            teamName,
            description,
            _uiState.value?.selectedImageUrl?.let { uploadImage(it) }
                ?: _uiState.value?.imageUrl.orEmpty()
        )

        _groupOperationResult.postValue(uiState.value?.key?.let {
            groupRepository.updateGroup(
                it,
                updatedGroupEntity
            )
        })
    }

    fun handleGroupEntity(title: String, description: String) {
        when (entryType) {
            PostEntryType.CREATE -> onClickCreate(title, description)
            PostEntryType.UPDATE -> onClickUpdate(title, description)
        }
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()

    private fun createGroupEntity(
        teamName: String,
        description: String,
        imageUrl: String
    ): GroupEntity {
        return GroupEntity(
            teamName = teamName,
            description = description,
            tags = _uiState.value?.tags?.map { it.name } ?: emptyList(),
            imageUrl = imageUrl
        )
    }

    fun handleGalleryResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val selectedImageUrl = data?.data
            _uiState.value = _uiState.value?.copy(selectedImageUrl = selectedImageUrl)
        }
    }

}
