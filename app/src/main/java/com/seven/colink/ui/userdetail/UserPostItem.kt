package com.seven.colink.ui.userdetail


sealed class UserPostItem{
    data class UserDetailPostItem(val userprojecting: String, val userprojectName: String, val userprojectTime: String): UserPostItem()
    data class UserDetailStudyItem(val userstudying: String, val userstudyName: String, val userstudyTime: String): UserPostItem()
}
