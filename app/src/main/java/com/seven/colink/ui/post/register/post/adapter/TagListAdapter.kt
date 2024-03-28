package com.seven.colink.ui.post.register.post.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.R
import com.seven.colink.databinding.ItemListGroupTagBinding
import com.seven.colink.databinding.ItemListPostTagBinding
import com.seven.colink.databinding.ItemListScheduleTagBinding
import com.seven.colink.databinding.ItemUnknownBinding
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.ui.post.register.post.model.TagListViewType

class TagListAdapter(
    private val onClickItem: (TagListItem) -> Unit
) : ListAdapter<TagListItem, TagListAdapter.TagViewHolder>(
    object : DiffUtil.ItemCallback<TagListItem>() {

        override fun areItemsTheSame(oldItem: TagListItem, newItem: TagListItem): Boolean =
            when {
                oldItem is TagListItem.Item && newItem is TagListItem.Item -> {
                    oldItem.name == newItem.name
                }

                oldItem is TagListItem.ContentItem && newItem is TagListItem.ContentItem -> {
                    oldItem.name == newItem.name
                }

                else -> oldItem == newItem

            }

        override fun areContentsTheSame(
            oldItem: TagListItem,
            newItem: TagListItem
        ): Boolean = oldItem == newItem

    }

) {
    abstract class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: TagListItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is TagListItem.Item -> TagListViewType.LIST_ITEM
        is TagListItem.ContentItem -> TagListViewType.CONTENT_ITEM
        is TagListItem.CustomItem -> TagListViewType.CUSTOM_ITEM
        else -> TagListViewType.UNKNOWN
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder =
        when (TagListViewType.from(viewType)) {
            TagListViewType.LIST_ITEM -> TagListItemViewHolder(
                ItemListPostTagBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickItem
            )

            TagListViewType.CONTENT_ITEM -> TagContentItemViewHolder(
                ItemListGroupTagBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            TagListViewType.CUSTOM_ITEM -> TagCustomItemViewHolder(
                ItemListScheduleTagBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickItem
            )

            else -> TagUnknownViewHolder(
                ItemUnknownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class TagListItemViewHolder(
        private val binding: ItemListPostTagBinding,
        private val onClickItem: (TagListItem) -> Unit
    ) : TagViewHolder(binding.root) {
        override fun onBind(item: TagListItem) {
            if (item is TagListItem.Item) {
                binding.tvTagName.text = item.name
                binding.ivTagDelete.setOnClickListener {
                    onClickItem(item)
                }
            }
        }
    }

    class TagContentItemViewHolder(
        private val binding: ItemListGroupTagBinding,
    ) : TagViewHolder(binding.root) {
        override fun onBind(item: TagListItem) {
            if (item is TagListItem.ContentItem) {
                binding.tvTagName.text = "# ${item.name}"
            }
        }
    }

    class TagCustomItemViewHolder(
        private val binding: ItemListScheduleTagBinding,
        private val onClickItem: (TagListItem) -> Unit
    ) : TagViewHolder(binding.root) {
        override fun onBind(item: TagListItem) {
            if (item is TagListItem.CustomItem) {
                val context = binding.root.context

                val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_tag)
                val tintColor =
                    ContextCompat.getColor(context, item.customColor ?: R.color.main_color)
                val alphaValue = 0.5f
                val tintedColor =
                    ColorUtils.setAlphaComponent(tintColor, (255 * alphaValue).toInt())
                backgroundDrawable?.setTint(tintedColor)
                binding.layoutSchedule.background = backgroundDrawable

                binding.tvTagName.text = item.name
                binding.ivTagDelete.setOnClickListener {
                    onClickItem(item)
                }
            }
        }
    }

    class TagUnknownViewHolder(binding: ItemUnknownBinding) :
        TagViewHolder(binding.root) {
        override fun onBind(item: TagListItem) = Unit
    }
}