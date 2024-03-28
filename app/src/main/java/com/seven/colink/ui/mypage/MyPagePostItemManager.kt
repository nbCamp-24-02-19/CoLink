package com.seven.colink.ui.mypage

object MyPagePostItemManager {
    private val PostItemList = listOf(
        MyPostItem.MyPagePostItem("참여중","코링","현재",""),
        MyPostItem.MyPagePostItem("참여중","코링","현재",""),
        MyPostItem.MyPagePostItem("참여중","코링","현재",""),
        MyPostItem.MyPagePostItem("중도하차","코링","현재",""),
        MyPostItem.MyPagePostItem("완료","코링","현재",""),
        MyPostItem.MyPageStudyItem("참여중","MVVM 공부","현재",""),
    )

    fun getItemAll() : List<MyPostItem> = PostItemList

}