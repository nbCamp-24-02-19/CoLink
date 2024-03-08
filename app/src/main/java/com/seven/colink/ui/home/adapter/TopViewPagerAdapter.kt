package com.seven.colink.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.databinding.ItemHomeTopBinding
import com.seven.colink.ui.home.TopItems

class TopViewPagerAdapter : ListAdapter<TopItems, TopViewPagerAdapter.TopViewHolder>(TopDiffUtil) {
    object TopDiffUtil : DiffUtil.ItemCallback<TopItems>() {
        override fun areItemsTheSame(oldItem: TopItems, newItem: TopItems): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: TopItems, newItem: TopItems): Boolean {
            return oldItem == newItem
        }
    }

    interface ItemClick {
        fun onClick(view: View, position: Int, item:TopItems)
    }

    var itemClick : ItemClick? = null

    inner class TopViewHolder(binding: ItemHomeTopBinding) : ViewHolder(binding.root) {
        val img = binding.ivTop
        val team = binding.tvTopTeam
        val date = binding.tvTopDate
        val title = binding.tvTopTitle

        fun onBind(item : TopItems) {
            img.load(item.img)
            team.text = "팀 스파르타"
            date.text = item.date
                ?.substring(2,10)
                ?.replace("-",".")
            title.text = item.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeTopBinding.inflate(inflater, parent, false)
        return TopViewHolder(binding)
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        holder.onBind(currentList[position])
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it, position, currentList[position])
        }
    }
}