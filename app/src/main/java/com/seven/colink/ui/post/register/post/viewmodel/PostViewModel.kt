package com.seven.colink.ui.post.register.post.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetChatRoomUseCase
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.domain.usecase.RegisterPostUseCase
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.ui.post.register.post.model.PostErrorMessage
import com.seven.colink.ui.post.register.post.model.PostErrorUiState
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.ui.post.register.post.model.PostListItem
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val userRepository: UserRepository,
    private val getChatRoomUseCase: GetChatRoomUseCase,
) : ViewModel() {

    private lateinit var entryType: PostEntryType
    private lateinit var entity: Post

    private val _uiState: MutableStateFlow<List<PostListItem>> = MutableStateFlow(emptyList())
    val uiState: StateFlow<List<PostListItem>> get() = _uiState.asStateFlow()

    private val _errorUiState: MutableStateFlow<PostErrorUiState> =
        MutableStateFlow(PostErrorUiState.init())
    val errorUiState: StateFlow<PostErrorUiState> get() = _errorUiState.asStateFlow()

    private val _complete = MutableSharedFlow<String>()
    val complete = _complete.asSharedFlow()

    private val postItemDataMap: MutableMap<String, String> = mutableMapOf()

    fun setEntryType(type: PostEntryType) {
        entryType = type
    }

    private fun updateUiState(newState: List<PostListItem>) {
        _uiState.value = newState
    }

    suspend fun setPostItem(entityKey: String? = null, groupType: GroupType? = null) {
        entity = getPostUseCase(entityKey ?: "") ?: Post(groupType = groupType)

        _uiState.value = entity.setUi()
    }

    private fun Post.setUi() = listOf(
        PostListItem.PostItem(
            key = key,
            title = title,
            imageUrl = imageUrl,
            groupType = groupType ?: GroupType.UNKNOWN,
            selectedImageUrl = null,
            description = description,
            descriptionMessage = groupType?.setDescriptionMessage(),
            tags = tags,
            registeredDate = registeredDate,
            view = views,
            memberIds = memberIds
        ),
        PostListItem.PostOptionItem(
            key = key,
            precautions = precautions,
            recruitInfo = recruitInfo,
            startDate = startDate,
            endDate = endDate
        ),
        PostListItem.TitleItem(
            R.string.people_recruited,
            R.string.limited_people,
        ),
        PostListItem.RecruitItem(
            key = key,
            recruit = recruit,
            groupType = groupType ?: GroupType.PROJECT,
            selectedCount = recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0
        ),
        PostListItem.ButtonItem(
            buttonText = R.string.bt_complete
        )
    )

    private fun GroupType.setDescriptionMessage() =
        if (this == GroupType.PROJECT) context.getString(R.string.input_content_project) else context.getString(
            R.string.input_content_study
        )


    fun checkValidAddTag(tag: String) {
        val validationResult = getValidAddTag(tag)
        if (validationResult == PostErrorMessage.PASS) {
            updatePostItem()
        }
        _errorUiState.value = errorUiState.value.copy(tag = validationResult)
    }

    private fun getValidAddTag(tag: String): PostErrorMessage {
        return uiState.value.firstOrNull()?.let {
            val tags = (it as? PostListItem.PostItem)?.tags.orEmpty()
            when {
                tags.size >= LIMITED_TAG_COUNT -> PostErrorMessage.TAG_MAX_COUNT
                tag in tags -> PostErrorMessage.TAG_ALREADY_EXIST
                else -> {
                    updateUiState(uiState.value.map { uiStateValue ->
                        when (uiStateValue) {
                            is PostListItem.PostItem -> uiStateValue.copy(tags = tags + tag)
                            else -> uiStateValue
                        }
                    })
                    PostErrorMessage.PASS
                }
            }
        } ?: PostErrorMessage.PASS
    }

    fun removeTagItem(tag: String?) {
        updatePostItem()

        updateUiState(uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.PostItem -> uiStateValue.copy(
                    tags = uiStateValue.tags?.filter { it != tag } ?: emptyList()
                )

                else -> uiStateValue
            }
        })
    }

    private suspend fun getCurrentUser(): String =
        authRepository.getCurrentUser().message

    private suspend fun Post.convertGroupEntity() = GroupEntity(
        key = key,
        postKey = key,
        authId = authId,
        teamName = runCatching {
            "${userRepository.getUserDetails(getCurrentUser()).getOrNull()?.name}님의 팀"
        }.getOrElse { "" },
        title = title,
        imageUrl = imageUrl,
        status = status,
        groupType = groupType ?: GroupType.UNKNOWN,
        description = description,
        tags = tags,
        precautions = precautions,
        memberIds = memberIds,
        registeredDate = registeredDate,
        startDate = startDate,
        endDate = endDate
    )

    fun setImageResult(data: Intent?) {
        updatePostItem()

        updateUiState(uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.PostItem -> uiStateValue.copy(selectedImageUrl = data?.data)
                else -> uiStateValue
            }
        })
    }

    private suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()

    fun arePostListItemFieldsValid() {
        val title = postItemDataMap["title"]
        val description = postItemDataMap["description"]
        val precautions = postItemDataMap["precautions"]
        val startDate = postItemDataMap["startDate"]
        val endDate = postItemDataMap["endDate"]

        val errorMessage = when {
            title.isNullOrBlank() -> PostErrorMessage.TITLE_BLANK
            description.isNullOrBlank() -> PostErrorMessage.DESCRIPTION_BLANK
            precautions.isNullOrBlank() -> PostErrorMessage.PRECAUTIONS_BLANK
            startDate.isNullOrBlank() || endDate.isNullOrBlank() -> PostErrorMessage.EXPECTED_SCHEDULE_BLANK
            else -> PostErrorMessage.PASS
        }

        val updatedErrorUiState = _errorUiState.value.copy(
            message = errorMessage
        )

        _errorUiState.value = updatedErrorUiState
    }


    fun addRecruitInfo(entity: RecruitInfo) {
        updateUiState(uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> {
                    val recruits = uiStateValue.recruit?.toMutableList() ?: mutableListOf()
                    recruits.add(entity)
                    uiStateValue.copy(recruit = recruits)
                }

                else -> uiStateValue
            }
        })
    }

    fun removeRecruitInfo(type: String?) {
        updateUiState(uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> uiStateValue.copy(
                    recruit = uiStateValue.recruit?.toMutableList()?.apply {
                        removeAll { it.type == type }
                    }
                )

                else -> uiStateValue
            }
        })
    }

    fun createPost(
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) = viewModelScope.launch {
        try {
            updatePostItem()

            val entity = if (entryType == PostEntryType.CREATE) {
                getCreatePostEntity()
            } else {
                getUpdatePostEntity()
            }

            if (entryType == PostEntryType.CREATE) {
                try {
                    registerPostUseCase(entity)
                    groupRepository.registerGroup(entity.convertGroupEntity())
                    onSuccess(context.getString(R.string.post_register_success))
                        getChatRoomUseCase(
                            key = entity.key,
                            uids = entity.memberIds,
                            title = entity.title!!,
                            type = when (entity.groupType) {
                                GroupType.PROJECT -> ChatTabType.PROJECT
                                GroupType.STUDY -> ChatTabType.STUDY
                                else -> return@launch
                            },
                            thumbnail = entity.imageUrl
                        )
                    _complete.emit(entity.key)
                } catch (groupException: Exception) {
                    onError(groupException)
                }
            } else {
                try {
                    registerPostUseCase(entity)
                    onSuccess(context.getString(R.string.post_update_success))
                } catch (e: Exception) {
                    onError(e)
                }
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    private suspend fun getCreatePostEntity(): Post {
        val postItem =
            _uiState.value.find { it is PostListItem.PostItem } as? PostListItem.PostItem

        return Post(
            authId = getCurrentUser(),
            title = postItem?.title,
            imageUrl = postItem?.selectedImageUrl?.let { uploadImage(it) } ?: postItem?.imageUrl,
            groupType = postItem?.groupType,
            description = postItem?.description,
            tags = postItem?.tags,
            precautions = _uiState.value.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.precautions },
            recruit = (_uiState.value.find { it is PostListItem.RecruitItem }
                    as? PostListItem.RecruitItem)?.recruit,
            memberIds = listOf(getCurrentUser()),
            startDate = _uiState.value.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.startDate },
            endDate = _uiState.value.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.endDate },
        )
    }

    private suspend fun getUpdatePostEntity(): Post {
        val postItem =
            _uiState.value.find { it is PostListItem.PostItem } as? PostListItem.PostItem
        val imageUrl = postItem?.selectedImageUrl?.let { uploadImage(it) } ?: postItem?.imageUrl

        return entity.copy(
            title = postItem?.title,
            imageUrl = imageUrl,
            description = postItem?.description,
            tags = postItem?.tags,
            precautions = _uiState.value.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.precautions },
            recruit = (_uiState.value.find { it is PostListItem.RecruitItem }
                    as? PostListItem.RecruitItem)?.recruit,
            memberIds = entity.memberIds,
            startDate = _uiState.value.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.startDate },
            endDate = _uiState.value.find { it is PostListItem.PostOptionItem }
                .let { (it as? PostListItem.PostOptionItem)?.endDate },
        )
    }

    fun incrementCount() {
        updateUiState(uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> {
                    val currentCount = uiStateValue.selectedCount ?: 0
                    uiStateValue.copy(selectedCount = currentCount + 1)
                }

                else -> uiStateValue
            }
        })
        updateRecruitBasedOnCount()
    }

    fun decrementCount() {
        updateUiState(uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> {
                    val currentCount = uiStateValue.selectedCount ?: 0
                    uiStateValue.copy(selectedCount = if (currentCount > 0) currentCount - 1 else 0)
                }

                else -> uiStateValue
            }
        })
        updateRecruitBasedOnCount()
    }

    private fun updateRecruitBasedOnCount() {
        updateUiState(uiState.value.map { uiStateValue ->
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
        })

        val updatedRecruit = uiState.value.find { it is PostListItem.RecruitItem }?.let {
            val recruitItem = it as? PostListItem.RecruitItem
            recruitItem?.recruit
        }
        updateRecruit(updatedRecruit)
    }

    private fun updateRecruit(updatedRecruit: List<RecruitInfo>?) {
        updateUiState(uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.RecruitItem -> uiStateValue.copy(recruit = updatedRecruit)
                else -> uiStateValue
            }
        })
    }

    fun updatePostItemText(position: Int, title: String, description: String) {
        if (position >= 0 && position < _uiState.value.size) {
            when (val uiStateValue = _uiState.value[position]) {
                is PostListItem.PostItem -> {
                    postItemDataMap["title"] = title
                    postItemDataMap["description"] = description
                }

                is PostListItem.PostOptionItem -> {
                    postItemDataMap["precautions"] = title
                    if (description.isNotBlank()) {
                        val (startDate, endDate) = description.split("~")
                        postItemDataMap["startDate"] = startDate
                        postItemDataMap["endDate"] = endDate
                    }
                }

                else -> uiStateValue
            }
        }
    }

    private fun updatePostItem() {
        updateUiState(_uiState.value.map { uiStateValue ->
            when (uiStateValue) {
                is PostListItem.PostItem -> {
                    val title = postItemDataMap["title"] ?: uiStateValue.title
                    val description = postItemDataMap["description"] ?: uiStateValue.description
                    uiStateValue.copy(title = title, description = description)
                }

                is PostListItem.PostOptionItem -> {
                    val precautions =
                        postItemDataMap["precautions"] ?: uiStateValue.precautions
                    val startDate =
                        postItemDataMap["startDate"] ?: uiStateValue.startDate
                    val endDate =
                        postItemDataMap["endDate"] ?: uiStateValue.endDate
                    uiStateValue.copy(precautions = precautions, startDate = startDate, endDate = endDate)
                }

                else -> uiStateValue
            }
        })
    }
}
