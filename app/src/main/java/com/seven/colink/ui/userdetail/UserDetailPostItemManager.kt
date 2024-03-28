package com.seven.colink.ui.userdetail

import com.seven.colink.ui.mypage.MyPostItem

object UserDetailPostItemManager {
    private val PostItemList = listOf(
        UserPostItem.UserDetailPostItem("참여중","코링","현재",""),
        UserPostItem.UserDetailPostItem("참여중","코링","현재",""),
        UserPostItem.UserDetailPostItem("참여중","코링","현재",""),
        UserPostItem.UserDetailPostItem("중도하차","코링","현재",""),
        UserPostItem.UserDetailPostItem("완료","코링","현재",""),
        UserPostItem.UserDetailStudyItem("참여중","MVVM 공부","현재",""),
    )
    fun getItemAll() : List<UserPostItem> = PostItemList
}