package com.seven.colink.ui.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemListPostTagBinding
import com.seven.colink.domain.entity.TagEntity

class TagListAdapter(
    private val onClickItem: (Int, TagEntity) -> Unit
) : ListAdapter<TagEntity, TagListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<TagEntity>() {

        override fun areItemsTheSame(oldItem: TagEntity, newItem: TagEntity): Boolean =
            oldItem.key == newItem.key

        override fun areContentsTheSame(
            oldItem: TagEntity,
            newItem: TagEntity
        ): Boolean = oldItem == newItem

    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemListPostTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemListPostTagBinding,
        private val onClickItem: (Int, TagEntity) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TagEntity) = with(binding) {
            tvTagName.text = item.name
            ivTagDelete.setOnClickListener {
                onClickItem(adapterPosition, item)
            }
        }
    }

}