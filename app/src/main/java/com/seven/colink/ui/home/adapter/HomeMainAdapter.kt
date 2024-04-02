package com.seven.colink.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.seven.colink.databinding.ItemHomeBottomBinding
import com.seven.colink.databinding.ItemHomeHeaderBinding
import com.seven.colink.databinding.ItemHomeTopViewpagerBinding
import com.seven.colink.ui.home.HomeAdapterItems
import com.seven.colink.ui.promotion.ProductPromotionActivity
import com.seven.colink.util.Constants

class HomeMainAdapter(private val context: Context) : ListAdapter<HomeAdapterItems, ViewHolder>(HomeMainDiffUtil) {
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
    private val PROMOTION_HEADER_TYPE = 1
    private val PROMOTION_TYPE = 2
    private val HEADER_TYPE = 3

    interface ItemClick {
        fun onClick(view: View,position: Int, items: HomeAdapterItems)
    }

    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TOP_TYPE -> {
                val topItem = ItemHomeTopViewpagerBinding.inflate(inflater, parent, false)
                TopViewHolder(topItem)
            }

            PROMOTION_HEADER_TYPE -> {
                val header = ItemHomeHeaderBinding.inflate(inflater, parent, false)
                PromotionHeaderViewHolder(header)
            }

            PROMOTION_TYPE->{
                val promotion = ItemHomeBottomBinding.inflate(inflater,parent,false)
                PromotionViewHolder(promotion)
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
            holder as TopViewHolder
            with(holder) {
                pager.adapter = item.adapter
                pager.post{
                    pager.setCurrentItem(1,false)
                    sum.text = pager.adapter?.itemCount?.minus(2).toString()
                }
            }
        }

        if (item is HomeAdapterItems.PromotionHeader) {
            holder as PromotionHeaderViewHolder
            holder.header.text = item.header
        }

        if (item is HomeAdapterItems.PromotionView) {
            holder as PromotionViewHolder

            holder.itemView.setOnClickListener{
                itemClick?.onClick(it,position,item)
                val intent = Intent(context,ProductPromotionActivity::class.java)
                intent.putExtra(Constants.EXTRA_ENTITY_KEY,item.info.key)
                context.startActivity(intent)
            }

            with(holder) {
                title.text = item.info.title
                des.text = item.info.des
                tag.visibility = View.GONE
                divider.visibility = View.GONE
                team.visibility = View.VISIBLE
                team.text = item.info.team
                img.load(item.info.img)
            }
        }

        if (item is HomeAdapterItems.GroupHeader) {
            holder as HeaderViewHolder
            holder.header.text = item.header
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeAdapterItems.TopView -> TOP_TYPE
            is HomeAdapterItems.PromotionHeader -> PROMOTION_HEADER_TYPE
            is HomeAdapterItems.PromotionView -> PROMOTION_TYPE
            is HomeAdapterItems.GroupHeader -> HEADER_TYPE
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

        private val handler = Handler(Looper.getMainLooper())
        private val autoScrollRunnable = object : Runnable {
            override fun run() {
                val nextPos = (currentPos + 1 ) % 7
                pos.text = (nextPos + 1).toString()
                pager.setCurrentItem(nextPos, true)
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
                            pager.adapter?.itemCount?.minus(2)
                                ?.let { pager.setCurrentItem(it,false) }
                        }else if (currentPos == pager.adapter?.itemCount?.minus(1)) {
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
                pager.adapter?.itemCount?.minus(2)?.let { pager.setCurrentItem(it,true) }
            } else {
                pager.setCurrentItem(currentPos -1,true)
            }
            handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY + PAGE_SCROLL_DELAY)
        }

        private fun handleRightButtonClick() {
            handler.removeCallbacks(autoScrollRunnable)
            if (currentPos == pager.adapter?.itemCount?.minus(2)) {
                pager.setCurrentItem(1,true)
            } else {
                pager.setCurrentItem(currentPos +1,true)
            }
            handler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY + PAGE_SCROLL_DELAY)
        }
    }

    inner class PromotionHeaderViewHolder(binding: ItemHomeHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvHomeHeader
    }

    inner class PromotionViewHolder(binding: ItemHomeBottomBinding) : ViewHolder(binding.root) {
        val title = binding.tvHomeBottomTitle
        val des = binding.tvHomeBottomDes
        val tag = binding.tvHomeBottomKind
        val divider = binding.viewHomeBottomDivider
        val team = binding.tvHomeBottomTeam
        val img = binding.ivHomeBottomThumubnail
    }

    inner class HeaderViewHolder(binding: ItemHomeHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvHomeHeader
    }
}