package com.seven.colink.ui.group.content

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.util.Constants
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.PostEntryType
import com.seven.colink.util.status.ProjectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupContentViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private lateinit var entryType: PostEntryType

    private val _uiState = MutableStateFlow<List<GroupContentItem>>(emptyList())
    val uiState: StateFlow<List<GroupContentItem>> = _uiState

    private val _errorUiState: MutableStateFlow<GroupErrorUiState?> =
        MutableStateFlow(GroupErrorUiState.init())
    val errorUiState: StateFlow<GroupErrorUiState?> = _errorUiState

    private val _complete = MutableSharedFlow<String>()
    val complete = _complete.asSharedFlow()

    fun setEntryType(type: PostEntryType) {
        entryType = type
    }

    suspend fun setEntity(key: String) {
        viewModelScope.launch {
            val entity = key.let { groupRepository.getGroupDetail(it).getOrNull() }
            _uiState.value = entity?.let {
                val items = mutableListOf<GroupContentItem>()
                items.add(
                    GroupContentItem.GroupContent(
                        key = it.key,
                        teamName = it.teamName,
                        groupType = it.groupType,
                        description = it.description,
                        tags = it.tags,
                        imageUrl = it.imageUrl,
                        selectedImageUrl = null,
                        groupTypeUiState = GroupTypeUiState.Project,
                        buttonUiState = if (entryType == PostEntryType.CREATE) ContentButtonUiState.Create else ContentButtonUiState.Update
                    )
                )

                items.add(GroupContentItem.GroupProjectStatus(key = it.key, status = it.status))
                items
            } ?: emptyList()
        }
    }

    fun handleGroupEntity(title: String, description: String) {
        when (entryType) {
            PostEntryType.CREATE -> onClickCreate(title, description)
            PostEntryType.UPDATE -> onClickUpdate(title, description)
        }
    }

    private fun onClickCreate(teamName: String, description: String) = viewModelScope.launch {
        val imageUrl = when (val uiStateValue = uiState.value.firstOrNull()) {
            is GroupContentItem.GroupContent -> uiStateValue.selectedImageUrl?.let { uploadImage(it) }
                .orEmpty()

            is GroupContentItem.GroupProjectStatus -> ""
            else -> ""
        }

        val newGroupEntity =
            createGroupEntity(teamName, description, imageUrl, uiState.value.firstOrNull())
        resultPerformance(groupRepository.registerGroup(newGroupEntity))
    }

    private fun onClickUpdate(teamName: String, description: String) = viewModelScope.launch {
        val projectStatus =
            uiState.value.find { it is GroupContentItem.GroupProjectStatus } as? GroupContentItem.GroupProjectStatus
        val groupContent =
            uiState.value.find { it is GroupContentItem.GroupContent } as? GroupContentItem.GroupContent

        val key = projectStatus?.key
        val updatedGroupEntity = createGroupEntity(
            teamName, description,
            groupContent?.selectedImageUrl?.let { uploadImage(it) }
                ?: groupContent?.imageUrl.orEmpty(),
            groupContent
        )

        if (key != null) {
            resultPerformance(groupRepository.updateGroupSection(key, updatedGroupEntity))
            resultPerformance(groupRepository.updateGroupStatus(key, projectStatus.status ?: ProjectStatus.RECRUIT))
        }
    }

    private fun resultPerformance(result: DataResultStatus) = viewModelScope.launch {
        when (result) {
            DataResultStatus.SUCCESS -> _complete.emit("success")
            DataResultStatus.FAIL -> _complete.emit("failed")
        }
    }

    private fun createGroupEntity(
        teamName: String,
        description: String,
        imageUrl: String,
        uiStateValue: GroupContentItem?
    ): GroupEntity {
        return when (uiStateValue) {
            is GroupContentItem.GroupContent -> GroupEntity(
                teamName = teamName,
                description = description,
                tags = uiStateValue.tags,
                imageUrl = imageUrl,
            )

            is GroupContentItem.GroupProjectStatus -> GroupEntity(
                teamName = teamName,
                description = description,
                tags = emptyList(),
                imageUrl = imageUrl,
                status = uiStateValue.status ?: ProjectStatus.RECRUIT
            )

            else -> GroupEntity(
                teamName = teamName,
                description = description,
                tags = emptyList(),
                imageUrl = imageUrl,
                status = ProjectStatus.RECRUIT
            )
        }
    }

    fun onChangedStatus(status: ProjectStatus) {
        _uiState.value = uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is GroupContentItem.GroupProjectStatus -> uiStateValue.copy(status = status)
                else -> uiStateValue
            }
        }
    }

    fun checkValidAddTag(tag: String) {
        _errorUiState.value = errorUiState.value?.copy(tag = getValidAddTag(tag))
    }

    private fun getValidAddTag(tag: String): GroupErrorMessage {
        val list = when (val uiStateValue = uiState.value.firstOrNull()) {
            is GroupContentItem.GroupContent -> uiStateValue.tags.orEmpty()
            is GroupContentItem.GroupProjectStatus -> emptyList()
            else -> emptyList()
        }

        return when {
            list.size >= Constants.LIMITED_TAG_COUNT -> GroupErrorMessage.TAG_MAX_COUNT
            list.any { it == tag } -> GroupErrorMessage.TAG_ALREADY_EXIST
            else -> {
                _uiState.value = uiState.value.map { uiStateValue ->
                    when (uiStateValue) {
                        is GroupContentItem.GroupContent -> uiStateValue.copy(tags = list + tag)
                        is GroupContentItem.GroupProjectStatus -> uiStateValue
                    }
                }
                GroupErrorMessage.PASS
            }
        }
    }

    fun removeTagItem(tag: String?) {
        _uiState.value = uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is GroupContentItem.GroupContent -> uiStateValue.copy(
                    tags = uiStateValue.tags?.filter { it != tag } ?: emptyList()
                )

                is GroupContentItem.GroupProjectStatus -> uiStateValue
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrNull().toString()

    fun setImageResult(data: Intent?) {
        _uiState.value = uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is GroupContentItem.GroupContent -> uiStateValue.copy(selectedImageUrl = data?.data)
                is GroupContentItem.GroupProjectStatus -> uiStateValue
            }
        }
    }
}
