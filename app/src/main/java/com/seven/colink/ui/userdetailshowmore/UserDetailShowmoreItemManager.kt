package com.seven.colink.ui.userdetailshowmore

import com.seven.colink.ui.userdetailshowmore.UserDetailShowmoreItem

object UserDetailShowmoreItemManager {
    private val PostItemList = listOf(
        UserDetailShowmoreItem.ShowMoreProjectItem("참여중","코링","현재","","dd","dd",2,"dd"),
        UserDetailShowmoreItem.ShowMoreProjectItem("참여중","코링","현재","","dd","dd",2,"dd"),
        UserDetailShowmoreItem.ShowMoreProjectItem("참여중","코링","현재","","dd","dd",2,"dd"),
        UserDetailShowmoreItem.ShowMoreProjectItem("중도하차","코링","현재","","dd","dd",2,"dd"),
        UserDetailShowmoreItem.ShowMoreProjectItem("완료","코링","현재","","dd","dd",2,"dd"),
        UserDetailShowmoreItem.ShowMoreStudyItem("참여중","MVVM 공부","현재","","dd","dd",2,"dd"),
    )

    fun getItemAll() : List<UserDetailShowmoreItem> = PostItemList

}