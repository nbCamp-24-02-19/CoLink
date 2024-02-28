package com.seven.colink.ui.mypage

import com.seven.colink.R

object MyPageSkilItemManager {

//    const val DEFAULT_ID:Int = 0
    const val PLUS_ID: Int = -1

    private val skilMyPageItem = listOf(
        MyPageItem.skilItems(1,R.drawable.ic_kotlin)
    )
    private val pulsMyPageItem = MyPageItem.plusItems(skilMyPageItem.size +1, R.drawable.ic_add_24)

    fun getAllItem(): List<MyPageItem> = skilMyPageItem + pulsMyPageItem


}