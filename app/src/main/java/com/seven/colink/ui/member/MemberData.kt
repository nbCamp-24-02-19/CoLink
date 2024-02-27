package com.seven.colink.ui.member

sealed class MemberData {
    data class MemberTitle(val title: String) : MemberData()
    data class MemberList(var projectName: String, var days: Int, var description: String, var tags: String) : MemberData()
    data class MemberAdd(val addGroup: String, val appliedGroup: String) : MemberData()
//    data class MemberTitle()
}