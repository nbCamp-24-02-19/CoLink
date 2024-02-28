package com.seven.colink.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.databinding.ItemHomeTopBinding

class TopViewPagerAdapter : ListAdapter<TopItems, TopViewPagerAdapter.TopViewHolder>(TopDiffUtil) {
    object TopDiffUtil : DiffUtil.ItemCallback<TopItems>() {
        override fun areItemsTheSame(oldItem: TopItems, newItem: TopItems): Boolean {
            return oldItem.team == newItem.team
        }

        override fun areContentsTheSame(oldItem: TopItems, newItem: TopItems): Boolean {
            return oldItem == newItem
        }
    }

    inner class TopViewHolder(binding: ItemHomeTopBinding) : ViewHolder(binding.root) {
        val img = binding.ivTop
        val team = binding.tvTopTeam
        val date = binding.tvTopDate
        val title = binding.tvTopTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHomeTopBinding.inflate(inflater, parent, false)
        return TopViewHolder(binding)
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        val item = currentList[position]

        with(holder) {
            img.load(item.img)
            team.text = item.team
            date.text = item.date
            title.text = item.title
        }
    }
}