package com.seven.colink.ui.sign.signup.adater

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemListPostTagBinding

class SkillAdapter(
    private val onClickItem: (String) -> Unit
) : ListAdapter<String, SkillAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
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
        private val onClickItem: (String) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) = with(binding) {
            tvTagName.text = item
            ivTagDelete.setOnClickListener {
                onClickItem(item)
            }
        }
    }

}
