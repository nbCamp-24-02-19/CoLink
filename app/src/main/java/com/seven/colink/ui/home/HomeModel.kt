package com.seven.colink.ui.home

import com.seven.colink.ui.home.adapter.TopViewPagerAdapter
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.util.UUID

data class TopItems (
    val img : String?,
    val team : String?,
    val date : String?,
    val title : String?,
    val key : String? = "POST_" + UUID.randomUUID().toString()
)

data class BottomItems (
    val typeId : GroupType?,
    val title : String?,
    val des : String?,
    val kind : List<String>?,
    val img : String?,
    val key : String? = "POST_" + UUID.randomUUID().toString(),
    val blind : ProjectStatus?,
    val complete : ProjectStatus?,
    val lv : String? = "0"
)

sealed class HomeAdapterItems {
    data class TopView(
        var adapter : TopViewPagerAdapter
    ) : HomeAdapterItems()

    data class Header(
        val header: String
    ) : HomeAdapterItems()
}
