package com.seven.colink.ui.group.content

data class GroupErrorUiState (
    val tag: GroupErrorMessage,
) {
    companion object {
        fun init() = GroupErrorUiState(
            tag = GroupErrorMessage.PASS
        )
    }
}