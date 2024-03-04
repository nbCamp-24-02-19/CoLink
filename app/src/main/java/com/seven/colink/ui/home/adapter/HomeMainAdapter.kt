package com.seven.colink.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.databinding.ItemHomeHeaderBinding
import com.seven.colink.databinding.ItemHomeTopViewpagerBinding
import com.seven.colink.ui.home.HomeAdapterItems
import kotlin.math.ceil

class HomeMainAdapter : ListAdapter<HomeAdapterItems, ViewHolder>(HomeMainDiffUtil) {
    object HomeMainDiffUtil : DiffUtil.ItemCallback<HomeAdapterItems>() {
        override fun areItemsTheSame(
            oldItem: HomeAdapterItems, newItem: HomeAdapterItems
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HomeAdapterItems, newItem: HomeAdapterItems
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val TOP_TYPE = 0
    private val HEADER_TYPE = 1
    private var bannerPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TOP_TYPE -> {
                val topItem = ItemHomeTopViewpagerBinding.inflate(inflater, parent, false)
                TopViewHolder(topItem)
            }

            else -> {
                val header = ItemHomeHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(header)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        if (item is HomeAdapterItems.TopView) {
            with(holder as TopViewHolder) {
                pager.adapter = item.adapter
                sum.text = item.adapter.currentList.size.toString()
            }
        }
        if (item is HomeAdapterItems.Header) {
            holder as HeaderViewHolder
            holder.header.text = item.header
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeAdapterItems.TopView -> TOP_TYPE
            is HomeAdapterItems.Header -> HEADER_TYPE
        }
    }

    inner class TopViewHolder(binding: ItemHomeTopViewpagerBinding) : ViewHolder(binding.root) {
        val pager = binding.vpHomeTop
        var pos = binding.tvHomeTopCurrent
        val sum = binding.tvHomeTopSum
        private val left = binding.btnHomeBack
        private val right = binding.btnHomeFront

        init {
            pager.post {
                bannerPosition = Int.MAX_VALUE / 2 - ceil(TopViewPagerAdapter().currentList.size.toDouble() / 2).toInt()
                pager.setCurrentItem(bannerPosition, false)

                left.setOnClickListener {
                    val current = pager.currentItem
                    pager.setCurrentItem(current - 1, false)
                }

                right.setOnClickListener {
                    val current = pager.currentItem
                    pager.setCurrentItem(current + 1, false)

                }

                pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        pos.text = ((position % 7) + 1).toString()
                    }
                })
            }
        }
    }

    inner class HeaderViewHolder(binding: ItemHomeHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvHomeHeader
    }
}