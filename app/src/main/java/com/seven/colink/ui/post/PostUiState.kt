package com.seven.colink.ui.post

import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.TagEntity

data class PostUiState(
    val tagList: List<TagEntity>,
    val recruitList: List<RecruitInfo>,
    val singleRecruitInfo: RecruitInfo?,
    val totalPersonnelCount: Int
) {
    companion object {
        fun init() = PostUiState(
            tagList = emptyList(),
            recruitList = emptyList(),
            singleRecruitInfo = RecruitInfo(),
            totalPersonnelCount = 0
        )
    }
}

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