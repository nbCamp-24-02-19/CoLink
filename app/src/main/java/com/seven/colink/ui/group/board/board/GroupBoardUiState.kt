package com.seven.colink.ui.group.board.board

import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.ui.post.content.model.ContentOwnerButtonUiState

data class GroupBoardUiState(
    val groupEntity: GroupEntity?,
    val buttonUiState: ContentOwnerButtonUiState?
) {
    companion object {
        fun init() = GroupBoardUiState(
            groupEntity = null,
            buttonUiState = null
        )
    }
}