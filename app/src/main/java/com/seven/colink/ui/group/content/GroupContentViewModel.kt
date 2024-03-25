package com.seven.colink.ui.group.content

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.util.Constants
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.PostEntryType
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
    private val postItemDataMap: MutableMap<String, String> = mutableMapOf()

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

                items.add(
                    GroupContentItem.GroupOptionItem(
                        key = it.key,
                        precautions = it.precautions,
                        recruitInfo = it.recruitInfo
                    )
                )

                items
            } ?: emptyList()
        }
    }

    fun onClickUpdate() = viewModelScope.launch {
        updateGroupContentItem()
        val groupContent =
            uiState.value.find { it is GroupContentItem.GroupContent } as? GroupContentItem.GroupContent

        val key = groupContent?.key
        val updatedGroupEntity = updateGroupItem()

        if (key != null) {
            resultPerformance(groupRepository.updateGroupSection(key, updatedGroupEntity))
        }
    }

    private fun resultPerformance(result: DataResultStatus) = viewModelScope.launch {
        when (result) {
            DataResultStatus.SUCCESS -> _complete.emit("success")
            DataResultStatus.FAIL -> _complete.emit("failed")
        }
    }

    private suspend fun updateGroupItem(): GroupEntity {
        val groupItem = _uiState.value.find { it is GroupContentItem.GroupContent} as? GroupContentItem.GroupContent
        val textItem = _uiState.value.find { it is GroupContentItem.GroupOptionItem} as? GroupContentItem.GroupOptionItem

        val imageUrl = groupItem?.selectedImageUrl?.let { uploadImage(it) }
            ?: groupItem?.imageUrl.orEmpty()
        return GroupEntity(
            teamName = groupItem?.teamName,
            description = groupItem?.description,
            tags = groupItem?.tags,
            imageUrl = imageUrl,
            precautions = textItem?.precautions,
            recruitInfo = textItem?.recruitInfo
        )
    }

    fun checkValidAddTag(tag: String) {
        val validationResult = getValidAddTag(tag)
        if (validationResult == GroupErrorMessage.PASS) {
            updateGroupContentItem()
        }
        _errorUiState.value = errorUiState.value?.copy(tag = validationResult)
    }

    private fun getValidAddTag(tag: String): GroupErrorMessage {
        val list = when (val uiStateValue = uiState.value.firstOrNull()) {
            is GroupContentItem.GroupContent -> uiStateValue.tags.orEmpty()
            else -> emptyList()
        }

        return when {
            list.size >= Constants.LIMITED_TAG_COUNT -> GroupErrorMessage.TAG_MAX_COUNT
            list.any { it == tag } -> GroupErrorMessage.TAG_ALREADY_EXIST
            else -> {
                _uiState.value = uiState.value.map { uiStateValue ->
                    when (uiStateValue) {
                        is GroupContentItem.GroupContent -> uiStateValue.copy(tags = list + tag)
                        else -> uiStateValue
                    }
                }
                GroupErrorMessage.PASS
            }
        }
    }

    fun removeTagItem(tag: String?) {
        updateGroupContentItem()
        _uiState.value = uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is GroupContentItem.GroupContent -> uiStateValue.copy(
                    tags = uiStateValue.tags?.filter { it != tag } ?: emptyList()
                )

                else -> uiStateValue
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrNull().toString()

    fun setImageResult(data: Intent?) {
        updateGroupContentItem()
        _uiState.value = uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is GroupContentItem.GroupContent -> uiStateValue.copy(selectedImageUrl = data?.data)
                else -> uiStateValue
            }
        }
    }

    fun updateGroupItemText(position: Int, title: String, description: String) {
        if (position >= 0 && position < _uiState.value.size) {
            when (_uiState.value[position]) {
                is GroupContentItem.GroupContent -> {
                    postItemDataMap["title"] = title
                    postItemDataMap["description"] = description
                }

                is GroupContentItem.GroupOptionItem -> {
                    postItemDataMap["precautions"] = title
                    postItemDataMap["recruitInfo"] = description
                }

            }
        }
    }

    private fun updateGroupContentItem() {
        _uiState.value = uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is GroupContentItem.GroupContent -> {
                    val title = postItemDataMap["title"] ?: uiStateValue.teamName
                    val description = postItemDataMap["description"] ?: uiStateValue.description
                    uiStateValue.copy(teamName = title, description = description)
                }

                is GroupContentItem.GroupOptionItem -> {
                    val precautions =
                        postItemDataMap["precautions"] ?: uiStateValue.precautions
                    val recruitInfo =
                        postItemDataMap["recruitInfo"] ?: uiStateValue.recruitInfo
                    uiStateValue.copy(precautions = precautions, recruitInfo = recruitInfo)
                }

            }
        }
    }
}
