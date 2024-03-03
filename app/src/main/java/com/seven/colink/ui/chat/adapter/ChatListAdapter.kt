package com.seven.colink.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemChatListBinding
import com.seven.colink.ui.chat.model.ChatListItem

class ChatListAdapter(
    private val onClick: (ChatListItem) -> Unit,
): ListAdapter<ChatListItem,ChatListAdapter.ChatListViewHolder>(
    object : DiffUtil.ItemCallback<ChatListItem>() {
        override fun areItemsTheSame(
            oldItem: ChatListItem,
            newItem: ChatListItem
        ) = oldItem.key == newItem.key

        override fun areContentsTheSame(
            oldItem: ChatListItem,
            newItem: ChatListItem
        ) = oldItem == newItem

    }
) {

    abstract class ChatListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: ChatListItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder =
        ChatViewHolder(
            ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick
        )

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
    class ChatViewHolder(
        private val binding: ItemChatListBinding,
        private val onClick: (ChatListItem) -> Unit,
    ): ChatListViewHolder(binding.root) {
        override fun onBind(item: ChatListItem) = with(binding){
            ivChatListProfile.load(item.thumbnail?: R.drawable.ic_profile)
            tvChatListName.text = item.title
            tvChatListMessage.text = item.message
            tvChatListTime.text = item.recentTime
            tvChatListCount.text = item.unreadCount.toString()
            tvChatListCount.isVisible = item.unreadCount != 0

            root.setOnClickListener {
                onClick(item)
            }
        }

    }
}
