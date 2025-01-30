package com.seven.colink.ui.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemSearchPostBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.util.convert.convertError
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import java.time.LocalDateTime
import java.time.OffsetDateTime

class SearchAdapter(val mItems: MutableList<SearchModel>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    interface ItemClick {
        fun onClick(item: SearchModel, position: Int)
    }

    var itemClick: ItemClick? = null

    inner class SearchViewHolder(binding: ItemSearchPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemBox = binding.linearLayout
        val thumbnail = binding.ivSearchItemThumbnail
        val project = binding.tvSearchItemProject
        val study = binding.tvSearchItemStudy
        val recruit = binding.tvSearchItemRecruit
        val recruitEnd = binding.tvSearchItemRecruitEnd
        val title = binding.tvSearchItemTitle
        val description = binding.tvSearchItemDescription
        val tag = binding.tvSearchItemTag1
        val poster = binding.tvSearchItemPoster
        val time = binding.tvSearchItemTime
        val heartCount = binding.tvSearchItemHeartCount
        val viewCount = binding.tvSearchItemViewCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemSearchPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = mItems[position]

        holder.itemBox.setOnClickListener {
            itemClick?.onClick(item, position)
        }

        when (item.groupType) {
            GroupType.PROJECT -> {
                holder.project.visibility = View.VISIBLE
                holder.study.visibility = View.GONE
            }
            else -> {
                holder.study.visibility = View.VISIBLE
                holder.project.visibility = View.GONE
            }
        }

        when (item.status) {
            ProjectStatus.RECRUIT -> {
                holder.recruit.visibility = View.VISIBLE
                holder.recruitEnd.visibility = View.GONE
            }
            else -> {
                holder.recruitEnd.visibility = View.VISIBLE
                holder.recruit.visibility = View.GONE
            }
        }
        holder.thumbnail.load(item.thumbnail)
        holder.title.text = item.title
        holder.description.text = item.description
        holder.tag.text = item.tags?.map { "# " + it }?.joinToString("   ", "", "")
        holder.poster.text = item.authId
        holder.time.text = item.registeredDate
        holder.heartCount.text = item.likes.toString()
        holder.viewCount.text = item.views.toString()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }
}