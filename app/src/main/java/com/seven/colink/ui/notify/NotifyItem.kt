package com.seven.colink.ui.notify

import android.graphics.drawable.Drawable
import com.seven.colink.domain.model.NotifyType
import com.seven.colink.ui.notify.viewmodel.FilterType

sealed interface NotifyItem {

    data class Filter(
        val state: FilterType
    ): NotifyItem
    data class ChatItem(
        val key: String?,
        val name: String?,
        val registeredDate: String?,
        val profileUrl: String?,
        val description: String?,
    ): NotifyItem

    data class DefaultItem(
        val key: String?,
        val type: NotifyType?,
        val icon: Drawable?,
        val iconBackground: Int?,
        val title: String?,
        val body: String?,
        val registeredDate: String?,
    ): NotifyItem
}