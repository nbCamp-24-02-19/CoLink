package com.seven.colink.ui.showmore

sealed class ShowMoreItem {
    data class ShowMoreProjectItem(
        val showmoreprojecting: String,
        val showmoreprojectTitle: String,
        val showmoreprojectTime: String,
        val projectKey: String,
        val showmoreprojectName: String,
        val showmoreprojectDescription: String,
        val showmoreprojectViewCount: Int = -1,
        val showmoreprojectImage: String,
    ):ShowMoreItem()
    data class ShowMoreStudyItem(
        val showmorestudying: String,
        val showmorestudytitle: String,
        val showmorestudyTime: String,
        val studyKey: String,
        val showmorestudyName: String,
        val showmorestudyDescription: String,
        val showmorestudyViewCount: Int = -1,
        val showmorestudyImage: String,
    ):ShowMoreItem()
}
