package com.seven.colink.ui.promotion.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemProductDesImgBinding
import com.seven.colink.databinding.ItemProductImgBinding
import com.seven.colink.databinding.ItemProductLinkBinding
import com.seven.colink.databinding.ItemProductMemberHeaderBinding
import com.seven.colink.databinding.ItemProductNewLinkBinding
import com.seven.colink.databinding.ItemProductNewTitleBinding
import com.seven.colink.databinding.ItemProductProjectHeaderBinding
import com.seven.colink.databinding.ItemProductTitleBinding
import com.seven.colink.ui.promotion.model.LinkItem
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.ui.promotion.model.ViewLinkImg
import com.seven.colink.ui.userdetail.UserDetailActivity
import com.seven.colink.ui.userdetail.UserDetailActivity.Companion.EXTRA_USER_KEY
import com.seven.colink.util.Constants
import com.seven.colink.util.setLevelIcon
import okhttp3.internal.notify

class ProductPromotionViewAdapter(private val context: Context) : ListAdapter<ProductPromotionItems, RecyclerView.ViewHolder>(
    ProductPromotionDiffUtil
) {
    object ProductPromotionDiffUtil : DiffUtil.ItemCallback<ProductPromotionItems>() {
        override fun areItemsTheSame(
            oldItem: ProductPromotionItems, newItem: ProductPromotionItems
        ): Boolean {
            return when {
                oldItem is ProductPromotionItems.Img && newItem is ProductPromotionItems.Img -> {
                    oldItem.img == newItem.img
                }

                oldItem is ProductPromotionItems.Title && newItem is ProductPromotionItems.Title -> {
                    oldItem == newItem
                }

                oldItem is ProductPromotionItems.ProjectLeaderItem && newItem is ProductPromotionItems.ProjectLeaderItem -> {
                    oldItem.userInfo?.getOrNull()?.uid == newItem.userInfo?.getOrNull()?.uid
                }

                oldItem is ProductPromotionItems.MiddleImg && newItem is ProductPromotionItems.MiddleImg -> {
                    oldItem.img == newItem.img
                }

                oldItem is ProductPromotionItems.Link && newItem is ProductPromotionItems.Link -> {
                    oldItem == newItem
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
        fun onClick(view: View, position: Int)
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
    private val NINETH_TYPE = 8

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            FIRST_TYPE -> {
                val first = ItemProductImgBinding.inflate(inflater,parent,false)
                FirstViewHolder(first)
            }
            SECOND_TYPE -> {
                val second = ItemProductNewTitleBinding.inflate(inflater,parent,false)
                TitleViewHolder(second)
            }
            THIRD_TYPE -> {
                val third = ItemProductDesImgBinding.inflate(inflater,parent,false)
                DesImgViewHolder(third)
            }
            FORTH_TYPE -> {
                val forth = ItemProductNewLinkBinding.inflate(inflater,parent,false)
                LinkViewHolder(forth)
            }
            FIFTH_TYPE -> {
                val fifth = ItemProductProjectHeaderBinding.inflate(inflater,parent,false)
                FifthViewHolder(fifth)
            }
            SIXTH_TYPE -> {
                val sixth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
                SixthViewHolder(sixth)
            }
            SEVENTH_TYPE -> {
                val seventh = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                SeventhViewHolder(seventh)
            }
            EIGHTH_TYPE -> {
                val eighth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
                EighthViewHolder(eighth)
            }
            else -> {
                val nineth = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                NinethViewHolder(nineth)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (item is ProductPromotionItems.Img) {
            holder as FirstViewHolder
            holder.img.load(item.img)
        }

        if (item is ProductPromotionItems.Title) {
            holder as TitleViewHolder
            with(holder) {
                tagAos.visibility = View.GONE
                tagIos.visibility = View.GONE

                title.text = item.title
                date.text = item.date
                team.text = item.team
                des.text = item.des

                if (!item.web.isNullOrEmpty()) {
                    imgWeb.visibility = View.VISIBLE
                }
                if (!item.aos.isNullOrEmpty()) {
                    imgAos.visibility = View.VISIBLE
                    tagAos.visibility = View.VISIBLE
                }
                if (!item.ios.isNullOrEmpty()) {
                    imgIos.visibility = View.VISIBLE
                    tagIos.visibility = View.VISIBLE
                }
            }
        }

        if (item is ProductPromotionItems.MiddleImg) {
            holder as DesImgViewHolder
            holder.desExam.visibility = View.GONE

            if (item.img.isNullOrEmpty()) {
                holder.lay.visibility = View.GONE
                holder.img.visibility = View.GONE
            }else {
                holder.lay.visibility = View.VISIBLE
                holder.img.visibility = View.VISIBLE
                holder.img.load(item.img)
            }
        }

        if (item is ProductPromotionItems.Link) {
            holder as LinkViewHolder

            with(holder) {
                web.text = item.webLink
                web.setOnClickListener {
                    val url = item.webLink
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }

                if (item.aosLink?.isNotEmpty() == true) {
                    aos.visibility = View.VISIBLE
                    aos.setOnClickListener {
                        val url = item.aosLink
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$url"))
                        context.startActivity(intent)
                    }
                }else {
                    aos.visibility = View.INVISIBLE
                }

                if (item.iosLink?.isNotEmpty() == true) {
                    ios.visibility = View.VISIBLE
                    ios.setOnClickListener {
                        val url = item.iosLink
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$url"))
                        context.startActivity(intent)
                    }
                }else {
                    ios.visibility = View.INVISIBLE
                }
            }
        }

        if (item is ProductPromotionItems.ProjectHeader) {
            holder as FifthViewHolder
            holder.header.text = "프로젝트 멤버"
        }

        if (item is ProductPromotionItems.ProjectLeaderHeader) {
            holder as SixthViewHolder
            holder.header.text = "리더"
        }

        if (item is ProductPromotionItems.ProjectLeaderItem) {
            holder as SeventhViewHolder
            holder.itemView.setOnClickListener {
                itemClick?.onClick(it,position)

                val intent = Intent(context,UserDetailActivity::class.java)
                intent.putExtra(EXTRA_USER_KEY,item.userInfo?.getOrNull()?.uid)
                context.startActivity(intent)
            }
            val items = item.userInfo
            with(holder) {
                img.load(items?.getOrNull()?.photoUrl)
                name.text = items?.getOrNull()?.name
                intro.text = items?.getOrNull()?.info
                grade.text = items?.getOrNull()?.grade.toString()
                level.text = items?.getOrNull()?.level.toString()
                items?.getOrNull()?.level.let { color ->
                    if (color != null) {
                        levelColor.setLevelIcon(color)
                    }
                }
            }
        }

        if (item is ProductPromotionItems.ProjectMemberHeader) {
            holder as EighthViewHolder
            holder.header.text = "멤버"
        }

        if (item is ProductPromotionItems.ProjectMember) {
            holder as NinethViewHolder
            holder.itemView.setOnClickListener {
                itemClick?.onClick(it,position)

                val intent = Intent(context,UserDetailActivity::class.java)
                intent.putExtra(EXTRA_USER_KEY,item.userInfo?.uid)
                context.startActivity(intent)
            }
            val items = item.userInfo
            with(holder) {
                items?.let { member ->
                    img.load(member.photoUrl)
                    name.text = member.name
                    intro.text = member.info
                    grade.text = member.grade.toString()
                    level.text = member.level.toString()
                    member.level.let { color ->
                        if (color != null) {
                            levelColor.setLevelIcon(color)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is ProductPromotionItems.Img -> FIRST_TYPE
            is ProductPromotionItems.Title -> SECOND_TYPE
            is ProductPromotionItems.MiddleImg -> THIRD_TYPE
            is ProductPromotionItems.Link -> FORTH_TYPE
            is ProductPromotionItems.ProjectHeader -> FIFTH_TYPE
            is ProductPromotionItems.ProjectLeaderHeader -> SIXTH_TYPE
            is ProductPromotionItems.ProjectLeaderItem -> SEVENTH_TYPE
            is ProductPromotionItems.ProjectMemberHeader -> EIGHTH_TYPE
            is ProductPromotionItems.ProjectMember -> NINETH_TYPE
        }
    }

    inner class FirstViewHolder(binding: ItemProductImgBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivProductPromotion
    }

    inner class TitleViewHolder(binding: ItemProductNewTitleBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.tvProductTitle
        val date = binding.tvRegisterDatetime
        val team = binding.tvTeam
        val des = binding.tvContent
        val tagAos = binding.tvTagAndroid
        val tagIos = binding.tvTagApple
        val imgWeb = binding.ivWeb
        val imgAos = binding.ivAos
        val imgIos = binding.ivIos
    }

    inner class DesImgViewHolder(binding: ItemProductDesImgBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivProductPromotion
        val lay = binding.layMiddle
        val desExam = binding.tvProductExampleDesImg
    }

    inner class LinkViewHolder(binding: ItemProductNewLinkBinding) : RecyclerView.ViewHolder(binding.root) {
        val web = binding.tvProductWebLink
        val aos = binding.ivPlaystore
        val ios = binding.ivAppstore
    }

    inner class FifthViewHolder(binding: ItemProductProjectHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionHeader
    }

    inner class SixthViewHolder(binding: ItemProductMemberHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader
    }

    inner class SeventhViewHolder(binding: ItemPostMemberInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
        val lay = binding.layMember
    }

    inner class EighthViewHolder(binding : ItemProductMemberHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader
    }

    inner class NinethViewHolder(binding : ItemPostMemberInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
        val lay = binding.layMember
    }
}