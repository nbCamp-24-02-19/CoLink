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
    val key : String?
)

data class BottomItems (
    val typeId : GroupType?,
    val title : String?,
    val des : String?,
    val kind : List<String>?,
    val img : String?,
    val key : String? = "POST_" + UUID.randomUUID().toString(),
    val blind : ProjectStatus? = ProjectStatus.RECRUIT,
    val complete : ProjectStatus? = ProjectStatus.RECRUIT,
    val lv : String? = "0"
)

data class MiddlePromotionItems(
    val title : String?,
    val des : String?,
    val team : String?,
//        val kind : List<String>?,
    val img : String?,
    val key: String?
)

sealed class HomeAdapterItems {
    data class TopView(
        var adapter : TopViewPagerAdapter
    ) : HomeAdapterItems()

    data class PromotionHeader(
        val header : String
    ) : HomeAdapterItems()

    data class PromotionView(
        val info : MiddlePromotionItems
    ) : HomeAdapterItems()

    data class GroupHeader(
        val header: String
    ) : HomeAdapterItems()
}
