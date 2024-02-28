package com.seven.colink.ui.home

data class TopItems (
    val img : String,
    val team : String,
    val date : String,
    val title : String
)

data class BottomItems (
    val typeId : String,
    val title : String,
    val des : String,
    val kind : String,
    val lv : String,
    val img : String,
    val blind : Boolean = false,
    val complete : Boolean = false
)

data class BottomView(
    var adapter : BottomViewPagerAdapter
)
sealed class HomeAdapterItems {
    data class TopView(
        var adapter : TopViewPagerAdapter
    ) : HomeAdapterItems()

    data class Header(
        val header: String
    ) : HomeAdapterItems()
}
