package com.seven.colink.ui.group.register

import android.net.Uri
import com.seven.colink.domain.entity.TagEntity
import com.seven.colink.util.status.GroupType

data class GroupUiState(
    val title: String?,
    val groupType: GroupType?,
    val description: String?,
    val tags: List<TagEntity>?,
    val imageUrl: String?,
    val selectedImageUrl: Uri?,
    val groupTypeUiState: GroupTypeUiState?,
    val buttonUiState: ContentButtonUiState?,
) {
    companion object {
        fun init() = GroupUiState(
            title = null,
            groupType = null,
            description = null,
            tags = emptyList(),
            imageUrl = null,
            selectedImageUrl = null,
            groupTypeUiState = null,
            buttonUiState = null
        )
    }
}

sealed interface GroupTypeUiState {
    data object Project : GroupTypeUiState
    data object Study : GroupTypeUiState
}

sealed interface ContentButtonUiState {
    data object Create : ContentButtonUiState
    data object Update : ContentButtonUiState
}