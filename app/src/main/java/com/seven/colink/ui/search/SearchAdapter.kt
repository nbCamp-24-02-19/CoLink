package com.seven.colink.ui.search

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemSearchPostBinding
import com.seven.colink.domain.entity.PostEntity

class SearchAdapter(val mItems: MutableList<PostEntity>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(binding: ItemSearchPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val project = binding.tvSearchItemProject
        val study = binding.tvSearchItemStudy
        val recruit = binding.tvSearchItemRecruit
        val recruitEnd = binding.tvSearchItemRecruitEnd
        val title = binding.tvSearchItemTitle
        val description = binding.tvSearchItemDescription
        val tag = binding.tvSearchItemTag1
        val poster = binding.tvSearchItemPoster
        val time = binding.tvSearchItemTime
        val heart = binding.ivSearchItemHeart
        val heartCount = binding.tvSearchItemHeartCount
        val viewCount = binding.tvSearchItemViewCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val binding =
            ItemSearchPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.recruit.visibility = View.VISIBLE
        holder.project.visibility = View.VISIBLE
        holder.title.text = "Title"
        holder.description.text = "Description"
        holder.tag.text = "Tags"
        holder.poster.text = "Poster"
        holder.time.text = "Time"
        holder.viewCount.text = "244"

        Log.d("Adapter", "viewCount = ${holder.viewCount}")
    }

    override fun getItemCount(): Int {
        return 7
    }

}