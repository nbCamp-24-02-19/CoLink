package com.seven.colink.ui.post

data class PostViewState(
    val isProjectSelected: Boolean? = null,
    val projectButtonTextColor: Int? = null,
    val studyButtonTextColor: Int? = null,
    val editTextContent: Int? = null
) {
    companion object {
        fun init() = PostViewState()
    }
}