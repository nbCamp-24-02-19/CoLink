package com.seven.colink.ui.group.board.board

import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.ui.post.content.model.ContentButtonUiState

data class GroupBoardUiState(
    val groupEntity: GroupEntity?,
    val buttonUiState: ContentButtonUiState?
) {
    companion object {
        fun init() = GroupBoardUiState(
            groupEntity = null,
            buttonUiState = null
        )
    }
}