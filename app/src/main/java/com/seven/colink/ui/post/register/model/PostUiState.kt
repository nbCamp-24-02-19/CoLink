package com.seven.colink.ui.post.register.model

import android.net.Uri
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.TagEntity

data class PostUiState(
    val tagList: List<TagEntity>?,
    val recruitList: List<RecruitInfo>?,
    val totalPersonnelCount: Int?
) {
    companion object {
        fun init() = PostUiState(
            tagList = emptyList(),
            recruitList = emptyList(),
            totalPersonnelCount = 0
        )
    }
}

data class PostViewState(
    val isProjectSelected: Boolean? = null,
    val projectButtonTextColor: Int? = null,
    val studyButtonTextColor: Int? = null,
    val editTextTitle: String? = null,
    val editTextContent: String? = null,
    val isUpdated: Boolean? = null
) {
    companion object {
        fun init() = PostViewState()
    }
}

data class ImageUiState(
    val originImage: Uri?,
    val newImage: Uri?
) {
    companion object {
        fun init() = ImageUiState(
            originImage = null,
            newImage = null
        )
    }
}