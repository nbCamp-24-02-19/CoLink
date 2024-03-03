package com.seven.colink.ui.home

import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus

data class TopItems (
    val img : String?,
    val team : String?,
    val date : String?,
    val title : String?,
    val key : String?
)

data class BottomItems (
//    val typeId : Int?,
    val typeId : GroupType?,
    val title : String?,
    val des : String?,
//    val kind : String?,
    val kind : List<String>?,
    val lv : String?,
    val img : String?,
    val key : String?,
//    val blind : Boolean,
//    val complete : Boolean
    val blind : ProjectStatus?,
    val complete : ProjectStatus?
)

sealed class HomeAdapterItems {
    data class TopView(
        var adapter : TopViewPagerAdapter
    ) : HomeAdapterItems()

    data class Header(
        val header: String
    ) : HomeAdapterItems()
}
