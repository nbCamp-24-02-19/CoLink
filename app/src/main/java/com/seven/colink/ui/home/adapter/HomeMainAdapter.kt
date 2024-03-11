package com.seven.colink.ui.home.adapter

import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.seven.colink.databinding.ItemHomeHeaderBinding
import com.seven.colink.databinding.ItemHomeTopViewpagerBinding
import com.seven.colink.ui.home.HomeAdapterItems

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
                pager.post{
                    pager.setCurrentItem(1,false)
                }
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

    companion object {
        private const val AUTO_SCROLL_DELAY = 2000L
        private const val PAGE_SCROLL_DELAY = 2000L
    }

    inner class TopViewHolder(binding: ItemHomeTopViewpagerBinding) : ViewHolder(binding.root) {
        val pager = binding.vpHomeTop
        var pos = binding.tvHomeTopCurrent
        val sum = binding.tvHomeTopSum

        private lateinit var left : ImageView
        private lateinit var right : ImageView

        private var currentState = 0
        private var currentPos = 0
        private var ignoreCallback = false

        private val handler = Handler()
        private val autoScrollRunnable = object : Runnable {
            override fun run() {
                val nextPos = (currentPos + 1 ) % 7
                pos.text = (nextPos + 1).toString()
                ignoreCallback = true
                pager.setCurrentItem(nextPos, true)
                ignoreCallback = false
                currentPos = nextPos
                handler.postDelayed(this, AUTO_SCROLL_DELAY + PAGE_SCROLL_DELAY)
            }
        }

        init {
            pager.post {
                left = binding.btnHomeBack
                right = binding.btnHomeFront

                left.setOnClickListener {
                    handleLeftButtonClick()
                }
                right.setOnClickListener {
                    handleRightButtonClick()
                }

                pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    @RequiresApi(Build.VERSION_CODES.Q)
                    override fun onPageSelected(position: Int) {
                        currentPos = position
                        pos.text = currentPos.toString()
                        super.onPageSelected(position)
                        startAutoScroll()

                        if (currentPos == 0) {
                            pager.setCurrentItem(7,false)
                        }else if (currentPos == 8) {
                            pager.setCurrentItem(1,false)
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        handleScrollState(state)
                        currentState = state
                        super.onPageScrollStateChanged(state)
                    }

                    private fun handleScrollState(state: Int) {
                        if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                            handler.removeCallbacks(autoScrollRunnable)
                        }

                        if (state == ViewPager2.SCROLL_STATE_IDLE && currentState == ViewPager2.SCROLL_STATE_DRAGGING) {
                            handler.removeCallbacks(autoScrollRunnable)
                            setNextItem()
                            handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY + PAGE_SCROLL_DELAY)
                        }
                    }

                    private fun setNextItem() {
                        if (currentState != ViewPager2.SCROLL_STATE_SETTLING){
                            handleSetNextItem()
                        }
                    }

                    private fun handleSetNextItem() {
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

        private fun startAutoScroll() {
            handler.removeCallbacks(autoScrollRunnable)
            handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY + PAGE_SCROLL_DELAY)
        }

        private fun handleLeftButtonClick() {
            handler.removeCallbacks(autoScrollRunnable)
            if (currentPos == 1) {
                pager.setCurrentItem(7, true)
            } else {
                pager.setCurrentItem(currentPos - 1, true)
            }
            handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY + PAGE_SCROLL_DELAY)
        }

        private fun handleRightButtonClick() {
            handler.removeCallbacks(autoScrollRunnable)
            if (currentPos == 7) {
                pager.setCurrentItem(1, true)
            } else {
                pager.setCurrentItem(currentPos + 1, true)
            }
            handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY + PAGE_SCROLL_DELAY)
        }
    }

    inner class HeaderViewHolder(binding: ItemHomeHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvHomeHeader
    }
}