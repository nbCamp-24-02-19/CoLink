package com.seven.colink.ui.post

import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.TagEntity

data class TagUiState(
    val list: List<TagEntity>
) {
    companion object {
        fun init() = TagUiState(
            list = emptyList()
        )
    }
}

data class RecruitUiState(
    val list: List<RecruitInfo>,
    val totalPersonnelCount: Int
) {
    companion object {
        fun init() = RecruitUiState(
            list = emptyList(),
            totalPersonnelCount = 0
        )
    }
}