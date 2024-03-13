package com.seven.colink.ui.promotion.adapter

import android.content.Context
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemProductPromotionDescriptionBinding
import com.seven.colink.databinding.ItemProductPromotionImgBinding
import com.seven.colink.databinding.ItemProductPromotionMemberHeaderBinding
import com.seven.colink.databinding.ItemProductPromotionProjectHeaderBinding
import com.seven.colink.databinding.ItemProductPromotionTitleBinding
import com.seven.colink.ui.promotion.ProductPromotionItems
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.showToast

class ProductPromotionEditAdapter(private val context: Context) : ListAdapter<ProductPromotionItems, RecyclerView.ViewHolder>(
    ProductPromotionDiffUtil
) {
    object ProductPromotionDiffUtil : DiffUtil.ItemCallback<ProductPromotionItems>() {
        override fun areItemsTheSame(
            oldItem: ProductPromotionItems, newItem: ProductPromotionItems
        ): Boolean {
            return when {
                oldItem is ProductPromotionItems.Img && newItem is ProductPromotionItems.Img -> {
                    oldItem.key == newItem.key
                }

                oldItem is ProductPromotionItems.Title && newItem is ProductPromotionItems.Title -> {
                    oldItem.link == newItem.link
                }

                oldItem is ProductPromotionItems.ProjectMemberItem && newItem is ProductPromotionItems.ProjectMemberItem -> {
                    oldItem.userInfo.uid == newItem.userInfo.uid
                }

                oldItem is ProductPromotionItems.Des && newItem is ProductPromotionItems.Des -> {
                    oldItem.key == newItem.key
                }

                oldItem is ProductPromotionItems.ProjectMember && newItem is ProductPromotionItems.ProjectMember -> {
                    oldItem.userInfo == newItem.userInfo
                }

                else -> {
                    oldItem == newItem
                }
            }
        }

        override fun areContentsTheSame(
            oldItem: ProductPromotionItems, newItem: ProductPromotionItems
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface ItemClick {
        fun onClick(view: View,position: Int)
    }

    var itemClick : ItemClick? = null

    private val FIRST_TYPE = 0
    private val SECOND_TYPE = 1
    private val THIRD_TYPE = 2
    private val FORTH_TYPE = 3
    private val FIFTH_TYPE = 4
    private val SIXTH_TYPE = 5
    private val SEVENTH_TYPE = 6
    private val EIGHTH_TYPE = 7

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            FIRST_TYPE -> {
                val first = ItemProductPromotionImgBinding.inflate(inflater,parent,false)
                FirstViewHolder(first)
            }
            SECOND_TYPE -> {
                val second = ItemProductPromotionTitleBinding.inflate(inflater,parent,false)
                SecondViewHolder(second)
            }
            THIRD_TYPE -> {
                val third = ItemProductPromotionDescriptionBinding.inflate(inflater,parent,false)
                ThirdViewHolder(third)
            }
            FORTH_TYPE -> {
                val forth = ItemProductPromotionProjectHeaderBinding.inflate(inflater,parent,false)
                ForthViewHolder(forth)
            }
            FIFTH_TYPE -> {
                val fifth = ItemProductPromotionMemberHeaderBinding.inflate(inflater,parent,false)
                FifthViewHolder(fifth)
            }
            SIXTH_TYPE -> {
                val sixth = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                SixthViewHolder(sixth)
            }
            SEVENTH_TYPE -> {
                val seventh = ItemProductPromotionMemberHeaderBinding.inflate(inflater,parent,false)
                SeventhViewHolder(seventh)
            }
            else -> {
                val eighth = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                EighthViewHolder(eighth)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (item is ProductPromotionItems.Img) {
            holder as FirstViewHolder
            holder.img.load(item.img)

            holder.img.setOnClickListener {
                itemClick?.onClick(it,position)
            }
        }

        if (item is ProductPromotionItems.Title) {
            holder as SecondViewHolder
            with(holder) {
                editTitle.setText(item.title)
                date.text = item.date

                when(item.link.size) {
                    0 -> {
                        with(lay1) {
                            tvLinkName.visibility = View.GONE
                            ivLinkDelete.visibility = View.GONE
                        }
                        with(lay2) {
                            tvLinkName.visibility = View.GONE
                            ivLinkDelete.visibility = View.GONE
                        }
                        with(lay3) {
                            tvLinkName.visibility = View.GONE
                            ivLinkDelete.visibility = View.GONE
                        }
                    }

                    1 -> {
                        with(lay1) {
                            tvLinkName.visibility = View.VISIBLE
                            ivLinkDelete.visibility = View.VISIBLE
                        }
                        with(lay2) {
                            tvLinkName.visibility = View.GONE
                            ivLinkDelete.visibility = View.GONE
                        }
                        with(lay3) {
                            tvLinkName.visibility = View.GONE
                            ivLinkDelete.visibility = View.GONE
                        }
                    }

                    2 -> {
                        with(lay1) {
                            tvLinkName.visibility = View.VISIBLE
                            ivLinkDelete.visibility = View.VISIBLE
                        }
                        with(lay2) {
                            tvLinkName.visibility = View.VISIBLE
                            ivLinkDelete.visibility = View.VISIBLE
                        }
                        with(lay3) {
                            tvLinkName.visibility = View.GONE
                            ivLinkDelete.visibility = View.GONE
                        }
                    }

                    3 -> {
                        with(lay1) {
                            tvLinkName.visibility = View.VISIBLE
                            ivLinkDelete.visibility = View.VISIBLE
                        }
                        with(lay2) {
                            tvLinkName.visibility = View.VISIBLE
                            ivLinkDelete.visibility = View.VISIBLE
                        }
                        with(lay3) {
                            tvLinkName.visibility = View.VISIBLE
                            ivLinkDelete.visibility = View.VISIBLE
                        }
                    }
                }

                editLink.setOnKeyListener { v, keyCode, event ->
                    val link = editLink.text.toString()

                    if (keyCode == KEYCODE_ENTER && editLink.text.isEmpty()) {
                        Toast.makeText(context,"텍스트를 입력 하세요",Toast.LENGTH_SHORT).show()
                        return@setOnKeyListener true
                    }
                    if (keyCode == KEYCODE_ENTER && event.action == ACTION_DOWN) {
                        item.link.add(link)

                        if (item.link.size > 3) {
                            item.link.removeAt(3)
                            Toast.makeText(context,"링크는 3개까지만 입력할 수 있습니다.",Toast.LENGTH_SHORT).show()
                        }
                        editLink.text.clear()
                        hideKeyboard(editLink)
                        return@setOnKeyListener false
                    }
                    return@setOnKeyListener false
                }

                lay1.ivLinkDelete.setOnClickListener {
                    item.link.removeAt(0)
                }
                lay2.ivLinkDelete.setOnClickListener {
                    item.link.removeAt(1)
                }
                lay3.ivLinkDelete.setOnClickListener {
                    item.link.removeAt(2)
                }
            }
        }

        if (item is ProductPromotionItems.Des) {
            holder as ThirdViewHolder
            holder.editDes.setText(item.des)
        }

        if (item is ProductPromotionItems.ProjectHeader) {
            holder as ForthViewHolder
            holder.header.text = "프로젝트 멤버"
        }

        if (item is ProductPromotionItems.ProjectLeaderHeader) {
            holder as FifthViewHolder
            holder.header.text = "리더"
        }

        if (item is ProductPromotionItems.ProjectMemberItem) {
            holder as SixthViewHolder
            with(holder) {
//                img.load(item.img)
//                name.text = item.name
//                intro.text = item.info
//                grade.text = item.grade?.toString()
//                level.text = item.level?.toString()
//                item.level?.let { levelColor.setLevelIcon(it) }

                img.load(item.userInfo.photoUrl)
                name.text = item.userInfo.name
                intro.text = item.userInfo.info
                grade.text = item.userInfo.grade?.toString()
                level.text = item.userInfo.level?.toString()
                item.userInfo.level?.let { levelColor.setLevelIcon(it) }
            }
        }

        if (item is ProductPromotionItems.ProjectMemberHeader) {
            holder as SeventhViewHolder
            holder.header.text = "멤버"
        }

        if (item is ProductPromotionItems.ProjectMember) {
            holder as EighthViewHolder
            with(holder) {
//                img.load(item.img)
//                name.text = item.name
//                intro.text = item.info
//                grade.text = item.grade?.toString()
//                level.text = item.level?.toString()
//                item.level?.let { levelColor.setLevelIcon(it) }

//                img.load(item.userInfo.photoUrl)
//                name.text = item.userInfo.name
//                intro.text = item.userInfo.info
//                grade.text = item.userInfo.grade?.toString()
//                level.text = item.userInfo.level?.toString()
//                item.userInfo.level?.let { levelColor.setLevelIcon(it) }

                item.userInfo.forEach {
                    img.load(it.photoUrl)
                    name.text = it.name
                intro.text = it.info
                grade.text = it.grade?.toString()
                level.text = it.level?.toString()
                it.level?.let { color ->
                    levelColor.setLevelIcon(color) }
                }
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is ProductPromotionItems.Img -> FIRST_TYPE
            is ProductPromotionItems.Title -> SECOND_TYPE
            is ProductPromotionItems.Des -> THIRD_TYPE
            is ProductPromotionItems.ProjectHeader -> FORTH_TYPE
            is ProductPromotionItems.ProjectLeaderHeader -> FIFTH_TYPE
            is ProductPromotionItems.ProjectMemberItem -> SIXTH_TYPE
            is ProductPromotionItems.ProjectMemberHeader -> SEVENTH_TYPE
            is ProductPromotionItems.ProjectMember -> EIGHTH_TYPE
        }
    }

    private fun hideKeyboard(view : View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    inner class FirstViewHolder(binding: ItemProductPromotionImgBinding) : ViewHolder(binding.root) {
        val img = binding.ivProductPromotion
    }

    inner class SecondViewHolder(binding : ItemProductPromotionTitleBinding) : ViewHolder(binding.root) {
        val editTitle = binding.etProductPromotionTitle
        val date = binding.tvProductPromotionDate
        val editLink = binding.etProductPromotionEditLink
        val lay1 = binding.inProductPromotionLink1
        val lay2 = binding.inProductPromotionLink2
        val lay3 = binding.inProductPromotionLink3

        init {
            with(lay1) {
                tvLinkName.visibility = View.GONE
                ivLinkDelete.visibility = View.GONE
            }
            with(lay2) {
                tvLinkName.visibility = View.GONE
                ivLinkDelete.visibility = View.GONE
            }
            with(lay3) {
                tvLinkName.visibility = View.GONE
                ivLinkDelete.visibility = View.GONE
            }
        }
    }

    inner class ThirdViewHolder(binding : ItemProductPromotionDescriptionBinding) : ViewHolder(binding.root) {
        val editDes = binding.etProductPromotionDes
    }

    inner class ForthViewHolder(binding: ItemProductPromotionProjectHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader
    }

    inner class FifthViewHolder(binding: ItemProductPromotionMemberHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionHeader
    }

    inner class SixthViewHolder(binding: ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
    }

    inner class SeventhViewHolder(binding : ItemProductPromotionMemberHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionHeader
    }

    inner class EighthViewHolder(binding : ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
    }
}