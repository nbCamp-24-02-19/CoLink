package com.seven.colink.ui.group.content

import android.net.Uri
import com.seven.colink.util.status.GroupType

sealed interface GroupTypeUiState {
    data object Project : GroupTypeUiState
    data object Study : GroupTypeUiState
}

sealed interface ContentButtonUiState {
    data object Create : ContentButtonUiState
    data object Update : ContentButtonUiState
}

sealed interface GroupContentItem {
    data class GroupContent(
        val key: String?,
        val teamName: String?,
        val groupType: GroupType?,
        val description: String?,
        val tags: List<String>?,
        val imageUrl: String?,
        val selectedImageUrl: Uri?,
        val groupTypeUiState: GroupTypeUiState?,
        val buttonUiState: ContentButtonUiState?,
    ): GroupContentItem

    data class GroupOptionItem(
        val key: String?,
        val precautions: String?,
        val recruitInfo: String?,
    ) : GroupContentItem

}