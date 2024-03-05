package com.seven.colink.ui.mypage

sealed class MyPageItem {
    data class skilItems(val Id: Int, val language: String, val languageIcon: Int):MyPageItem()
    data class plusItems(val Id: Int, val plusIcon: Int):MyPageItem()
}
