package com.seven.colink.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.databinding.ItemHomeHeaderBinding
import com.seven.colink.databinding.ItemHomeTopViewpagerBinding
import com.seven.colink.ui.home.HomeAdapterItems
import com.seven.colink.ui.home.TopItems
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

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]

        if (item is HomeAdapterItems.TopView) {
            with(holder as TopViewHolder) {
                pager.adapter = item.adapter
                sum.text = "7"
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
                pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    var currentState = 0
                    var currentPos = 0

                    override fun onPageSelected(position: Int) {
                        currentPos = position
                        pos.text = (currentPos + 1).toString()
                        super.onPageSelected(position)

                        left.setOnClickListener {
                            if (currentPos == 0) {
                                pager.setCurrentItem(6,false)
                            }else{
                                pager.setCurrentItem(currentPos -1,false)
                            }
                        }

                        right.setOnClickListener {
                            if (currentPos == 6) {
                                pager.setCurrentItem(0,false)
                            }else{
                                pager.setCurrentItem(currentPos +1, false)
                            }
                        }

                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        handleScrollState(state)
                        currentState = state
                        super.onPageScrollStateChanged(state)
                    }

                    fun handleScrollState(state: Int) {
                        if (state == ViewPager2.SCROLL_STATE_IDLE && currentState == ViewPager2.SCROLL_STATE_DRAGGING) {
                            setNextItemIfNeeded()
                        }
                    }

                    fun setNextItemIfNeeded() {
                        if (currentState != ViewPager2.SCROLL_STATE_SETTLING){
                            handleSetNextItem()
                        }
                    }

                    fun handleSetNextItem() {
                        val lastPosition = pager.adapter?.itemCount?.minus(1)

                        if (currentPos == 0) {
                            if (lastPosition != null) {
                                pager.setCurrentItem(lastPosition, false)
                            }
                        } else if (currentPos == lastPosition) {
                            pager.setCurrentItem(0, false)
                        }
                    }
                })
            }
        }
    }

    inner class HeaderViewHolder(binding: ItemHomeHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvHomeHeader
    }
}