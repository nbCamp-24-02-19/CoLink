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
import com.seven.colink.databinding.ItemChatMyMessageBinding
import com.seven.colink.databinding.ItemChatOtherMessageBinding
import com.seven.colink.databinding.UnknownItemBinding
import com.seven.colink.ui.chat.model.ChatRoomItem
import com.seven.colink.ui.chat.type.ChatRoomItemType
import com.seven.colink.ui.chat.type.MessageState

class ChatRoomAdapter(

): ListAdapter<ChatRoomItem, ChatRoomAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<ChatRoomItem>(){
        override fun areItemsTheSame(
            oldItem: ChatRoomItem,
            newItem: ChatRoomItem
        )= when {
            oldItem is ChatRoomItem.MyMessage && newItem is ChatRoomItem.MyMessage -> {
                oldItem.key == newItem.key
            }

            oldItem is ChatRoomItem.OtherMessage && newItem is ChatRoomItem.OtherMessage -> {
                oldItem.key == newItem.key
            }

            else -> false
        }

        override fun areContentsTheSame(
            oldItem: ChatRoomItem,
            newItem: ChatRoomItem
        )= when {
            oldItem is ChatRoomItem.MyMessage && newItem is ChatRoomItem.MyMessage -> {
                oldItem == newItem
            }

            oldItem is ChatRoomItem.OtherMessage && newItem is ChatRoomItem.OtherMessage -> {
                oldItem == newItem
            }

            else -> false
        }

    }
) {

    abstract class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: ChatRoomItem)
        abstract fun setState(state: MessageState)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)){
        is ChatRoomItem.MyMessage -> ChatRoomItemType.MY
        is ChatRoomItem.OtherMessage -> ChatRoomItemType.OTHER
    }.ordinal
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = when(ChatRoomItemType.from(viewType)) {
        ChatRoomItemType.MY -> {
            MyMessageViewHolder(
                ItemChatMyMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
        ChatRoomItemType.OTHER -> {
            OtherMessageViewHolder(
                ItemChatOtherMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
        else -> UnknownViewHolder(
            UnknownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class OtherMessageViewHolder(
        private val binding: ItemChatOtherMessageBinding
    ) : ViewHolder(binding.root) {
        override fun onBind(item: ChatRoomItem) = with(binding) {
            if (item !is ChatRoomItem.OtherMessage) return@with
            ivChatProfile.load(item.profileUrl?: R.drawable.ic_profile)
            tvChatAuthName.text = item.name
            tvChatViewCount.text = item.viewCount.toString()
            tvChatViewCount.isVisible = item.viewCount != 0
            tvChatMessage.text = item.text
            tvChatRegisterTime.text = item.time
        }

        override fun setState(state: MessageState) = with(binding){
            when(state){
                MessageState.FIRST -> tvChatAuthName.isVisible = true
                MessageState.MIDDLE -> {}
                MessageState.LAST -> {
                    cvChatProfileView.isVisible = true
                    tvChatViewCount.isVisible = true
                    tvChatRegisterTime.isVisible = true
                }
                else -> {
                    tvChatAuthName.isVisible = true
                    cvChatProfileView.isVisible = true
                    tvChatViewCount.isVisible = true
                    tvChatRegisterTime.isVisible = true
                }
            }
        }

    }

    class MyMessageViewHolder(
        private val binding: ItemChatMyMessageBinding
    ): ViewHolder(binding.root) {
        override fun onBind(item: ChatRoomItem) = with(binding){
            if (item !is ChatRoomItem.MyMessage) return@with
            tvChatMyMessage.text = item.text
            tvChatMyRegisterTime.text = item.time
            tvChatMyViewCount.text = item.viewCount.toString()
            tvChatMyViewCount.isVisible = item.viewCount != 0
        }

        override fun setState(state: MessageState) = with(binding) {
            when(state) {
                MessageState.FIRST -> {}
                MessageState.MIDDLE -> {}
                MessageState.LAST -> {
                    tvChatMyRegisterTime.isVisible = true
                    tvChatMyViewCount.isVisible= true
                }
                else -> {
                    tvChatMyRegisterTime.isVisible = true
                    tvChatMyViewCount.isVisible= true
                }
            }
        }

    }

    class UnknownViewHolder(
        private val binding: UnknownItemBinding
    ) : ViewHolder(binding.root) {
        override fun onBind(item: ChatRoomItem) = with(binding) {

        }

        override fun setState(state: MessageState) {

        }

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        val isPreviousItemSameType = position > 0 && getItem(position - 1)::class == item::class
        val isNextItemSameType = position < itemCount - 1 && getItem(position + 1)::class == item::class

        when (holder) {
            is MyMessageViewHolder -> {
                when {
                    !isPreviousItemSameType && isNextItemSameType -> holder.setState(MessageState.FIRST)
                    isPreviousItemSameType && isNextItemSameType -> holder.setState(MessageState.MIDDLE)
                    isPreviousItemSameType && !isNextItemSameType -> holder.setState(MessageState.LAST)
                    else -> holder.setState(MessageState.DEFAULT)
                }
            }
            is OtherMessageViewHolder -> {
                when {
                    !isPreviousItemSameType && isNextItemSameType -> holder.setState(MessageState.FIRST)
                    isPreviousItemSameType && isNextItemSameType -> holder.setState(MessageState.MIDDLE)
                    isPreviousItemSameType && !isNextItemSameType -> holder.setState(MessageState.LAST)
                    else -> holder.setState(MessageState.DEFAULT)
                }
            }
        }
        holder.onBind(getItem(position))
    }
}