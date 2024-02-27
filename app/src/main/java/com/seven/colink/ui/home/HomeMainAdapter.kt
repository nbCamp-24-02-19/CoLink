package com.seven.colink.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.seven.colink.databinding.ItemHomeHeaderBinding
import com.seven.colink.databinding.ItemHomeTopViewpagerBinding

class HomeMainAdapter : ListAdapter<HomeAdapterItems, ViewHolder>(HomeMainDiffUtil) {
    object HomeMainDiffUtil : DiffUtil.ItemCallback<HomeAdapterItems>() {
        override fun areItemsTheSame(
            oldItem: HomeAdapterItems, newItem: HomeAdapterItems): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HomeAdapterItems, newItem: HomeAdapterItems): Boolean {
            return oldItem == newItem
        }
    }

    private val TOP_TYPE = 0
    private val HEADER_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType) {
            TOP_TYPE -> {
                val topItem = ItemHomeTopViewpagerBinding.inflate(inflater,parent,false)
                TopViewHolder(topItem)
            }
            else -> {
                val header = ItemHomeHeaderBinding.inflate(inflater,parent,false)
                HeaderViewHolder(header)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        if (item is HomeAdapterItems.TopView) {
            with(holder as TopViewHolder) {
                pager.adapter = item.adapter
                pos.text = item.toString()
                sum.text = currentList.size.toString()
            }
        }
        if (item is HomeAdapterItems.Header) {
            holder as HeaderViewHolder
            holder.header.text = item.header
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is HomeAdapterItems.TopView -> TOP_TYPE
            is HomeAdapterItems.Header -> HEADER_TYPE
        }
    }

    inner class TopViewHolder(binding : ItemHomeTopViewpagerBinding) : ViewHolder(binding.root) {
        val pager = binding.vpHomeTop
        val pos = binding.tvHomeTopCurrent
        val sum = binding.tvHomeTopSum
        private val left = binding.btnHomeBack
        private val right = binding.btnHomeFront

        init {
            left.setOnClickListener {
                val current = pager.currentItem
                if (current == 0) {
                    pager.setCurrentItem(6,false)
                }else {
                    pager.setCurrentItem(current-1,false)
                }
            }

            right.setOnClickListener {
                val current = pager.currentItem
                if (current == 6) {
                    pager.setCurrentItem(0,false)
                }else {
                    pager.setCurrentItem(current+1,false)
                }
            }

        }
    }

    inner class HeaderViewHolder (binding : ItemHomeHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvHomeHeader
    }
}