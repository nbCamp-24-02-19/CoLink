package com.seven.colink.ui.mypage

sealed class MyPageData{
    data class MyPageName(var Profile: Int, var Level: Int, var Name: String, var specialization: String):MyPageData()
//    data class MyPageSkil(var text: String):MyPageData()
    data class MyPageAboutMe(var AboutMe: String):MyPageData()
    data class MyPagePost(var parti: String, var projectName: String, var projectTime: String):MyPageData()
    data class MyPageTerms(var tName: String):MyPageData()
}
