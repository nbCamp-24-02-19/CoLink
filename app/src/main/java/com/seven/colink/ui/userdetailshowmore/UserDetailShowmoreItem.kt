package com.seven.colink.ui.userdetailshowmore

import com.seven.colink.ui.showmore.ShowMoreItem

sealed class UserDetailShowmoreItem {
    data class ShowMoreProjectItem(
        val showmoreprojecting: String,
        val showmoreprojectTitle: String,
        val showmoreprojectTime: String,
        val projectKey: String,
        val showmoreprojectName: String,
        val showmoreprojectDescription: String,
        val showmoreprojectViewCount: Int = -1,
        val showmoreprojectImage: String,
    ): UserDetailShowmoreItem()
    data class ShowMoreStudyItem(
        val showmorestudying: String,
        val showmorestudytitle: String,
        val showmorestudyTime: String,
        val studyKey: String,
        val showmorestudyName: String,
        val showmorestudyDescription: String,
        val showmorestudyViewCount: Int = -1,
        val showmorestudyImage: String,
    ): UserDetailShowmoreItem()
}