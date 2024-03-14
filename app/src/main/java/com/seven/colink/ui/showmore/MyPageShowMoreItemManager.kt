package com.seven.colink.ui.showmore

import com.seven.colink.ui.mypage.MyPostItem

object MyPageShowMoreItemManager {
    private val PostItemList = listOf(
        ShowMoreItem.ShowMoreProjectItem("참여중","코링","현재","","dd","dd",2,"dd"),
        ShowMoreItem.ShowMoreProjectItem("참여중","코링","현재","","dd","dd",2,"dd"),
        ShowMoreItem.ShowMoreProjectItem("참여중","코링","현재","","dd","dd",2,"dd"),
        ShowMoreItem.ShowMoreProjectItem("중도하차","코링","현재","","dd","dd",2,"dd"),
        ShowMoreItem.ShowMoreProjectItem("완료","코링","현재","","dd","dd",2,"dd"),
        ShowMoreItem.ShowMoreStudyItem("참여중","MVVM 공부","현재","","dd","dd",2,"dd"),
    )

    fun getItemAll() : List<ShowMoreItem> = PostItemList

}