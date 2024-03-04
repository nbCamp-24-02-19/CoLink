package com.seven.colink.ui.userdetail

data class UserDetailModel (
    val userName: String? = null,
    val userProfile: String? = null,
    val userLevel: Int? = 0,
    val userMainSpecialty: String? = null,
    val userBlog: String? = null,
    val userGit: String? = null,
    val userSkill: List<String>? = emptyList(),
    val userInfo: String? = null
)