package com.seven.colink.ui.Group

import android.widget.LinearLayout

sealed class GroupData {
    data class GroupTitle(val title: String) : GroupData()
    data class GroupList(
        var projectName: String,
        var days: Int,
        var description: String,
        var tags: String
    ) : GroupData()

    data class GroupAdd(val addGroup: String , val appliedGroup: String) : GroupData()
    data class GroupWant(
        var groupType: String,
        var title: String,
        var description: String,
        var kind: String,
        var lv: String,
        var img: Int
    ) : GroupData()
}