package com.seven.colink.ui.notify.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.seven.colink.R
import com.seven.colink.databinding.ItemNotificationChatBinding
import com.seven.colink.databinding.ItemNotificationDefaultBinding
import com.seven.colink.databinding.ItemNotificationFilterBinding
import com.seven.colink.ui.notify.NotifyItem
import com.seven.colink.ui.notify.NotifyItem.ChatItem
import com.seven.colink.ui.notify.NotifyItem.DefaultItem
import com.seven.colink.ui.notify.NotifyItem.Filter
import com.seven.colink.ui.notify.adapter.NotificationAdapter.ViewHolder
import com.seven.colink.ui.notify.type.NotifyType
import com.seven.colink.ui.notify.type.NotifyType.CHAT
import com.seven.colink.ui.notify.type.NotifyType.DEFAULT
import com.seven.colink.ui.notify.type.NotifyType.FILTER
import com.seven.colink.ui.notify.viewmodel.*

class NotificationAdapter (
    private val onChat: (String) -> Unit,
    private val selectedFilter: (FilterType) -> Unit,
    private val deleteAll: () -> Unit
): ListAdapter<NotifyItem, ViewHolder>(
    object : DiffUtil.ItemCallback<NotifyItem>() {
        override fun areItemsTheSame(oldItem: NotifyItem, newItem: NotifyItem) = when {
            oldItem is ChatItem && newItem is ChatItem -> oldItem.key == newItem.key
            oldItem is DefaultItem && newItem is DefaultItem -> oldItem.key == newItem.key
            else -> false
        }

        override fun areContentsTheSame(oldItem: NotifyItem, newItem: NotifyItem) =
            oldItem == newItem
    }
) {
    abstract class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: NotifyItem)
    }

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            is Filter -> FILTER
            is ChatItem -> CHAT
            is DefaultItem -> DEFAULT
        }.ordinal
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (NotifyType.from(viewType)) {
        FILTER -> {
            FilterViewHolder(
                ItemNotificationFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                selectedFilter,
                deleteAll
            )
        }
        CHAT -> {
            ChatNotifyViewHolder(
                ItemNotificationChatBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onChat,
            )
        }
        DEFAULT -> {
            DefaultNotifyViewHolder(
                ItemNotificationDefaultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    class FilterViewHolder(
        private val binding: ItemNotificationFilterBinding,
        private val selectedFilter: (FilterType) -> Unit,
        private val deleteAll: () -> Unit,
    ): ViewHolder(binding.root) {
        override fun onBind(item: NotifyItem) = with(binding) {
            item as Filter
            cvNotifyAll.setup(FilterType.ALL, tvNotifyAll, item.state)
            cvNotifyChat.setup(FilterType.CHAT, tvNotifyChat, item.state)
            cvNotifyRecruit.setup(FilterType.RECRUIT, tvNotifyRecruit, item.state)
            tvNotifyDelete.setOnClickListener { deleteAll }
        }

        private fun MaterialCardView.setup(filterType: FilterType, text: TextView, state: FilterType) {
            selected(text, isSelected = state == filterType)
            setOnClickListener { selectedFilter(filterType) }
        }

        private fun MaterialCardView.selected(text: TextView, isSelected: Boolean) {
            strokeColor = context.getColor(
                if (isSelected) R.color.main_color else R.color.enable_stroke
            )
            text.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.main_color else R.color.typo_color
                ))
        }
    }


    class ChatNotifyViewHolder(
        private val binding: ItemNotificationChatBinding,
        private val onChat: (String) -> Unit,
    ): ViewHolder(binding.root) {
        override fun onBind(item: NotifyItem) = with(binding) {
            if (item !is ChatItem) return@with
            ivNotifyProfile.load(item.profileUrl)
            tvNotifyName.text = item.name
            tvNotifyTime.text = item.registeredDate
            tvNotifyDescription.text = item.description

            root.setOnClickListener {
                item.key?.let { key -> onChat(key) }
            }
        }
    }

    class DefaultNotifyViewHolder(
        private val binding: ItemNotificationDefaultBinding
    ) :
        ViewHolder(binding.root) {
        override fun onBind(item: NotifyItem) = with(binding) {
            if (item !is DefaultItem) return@with
            ivNotifyIcon.background.setTint(item.iconBackground?: return@with)
            ivNotifyIcon.load(item.icon)
            tvNotifyTitle.text = item.title
            tvNotifyBody.text = item.body
            tvNotifyRegisteredDate.text = item.registeredDate
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
