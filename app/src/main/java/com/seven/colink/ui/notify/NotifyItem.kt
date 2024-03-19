package com.seven.colink.ui.notify

import android.graphics.drawable.Drawable

sealed interface NotifyItem {

    data object Filter: NotifyItem
    data class ChatItem(
        val key: String?,
        val name: String?,
        val registeredDate: String?,
        val profileUrl: String?,
        val description: String?,
    ): NotifyItem

    data class DefaultItem(
        val key: String?,
        val icon: Drawable?,
        val iconBackground: Int,
        val title: String?,
        val body: String?,
        val registeredDate: String?,
    ): NotifyItem
}