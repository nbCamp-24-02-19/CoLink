package com.seven.colink.ui.promotion.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemProductDesImgBinding
import com.seven.colink.databinding.ItemProductImgBinding
import com.seven.colink.databinding.ItemProductMemberHeaderBinding
import com.seven.colink.databinding.ItemProductNewLinkBinding
import com.seven.colink.databinding.ItemProductNewTitleBinding
import com.seven.colink.databinding.ItemProductProjectHeaderBinding
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.ui.userdetail.UserDetailActivity
import com.seven.colink.ui.userdetail.UserDetailActivity.Companion.EXTRA_USER_KEY
import com.seven.colink.util.convert.convertGradeFormat
import com.seven.colink.util.setLevelIcon

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
                MainImgViewHolder(first)
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
                ProjectHeaderViewHolder(fifth)
            }
            SIXTH_TYPE -> {
                val sixth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
                LeaderHeaderViewHolder(sixth)
            }
            SEVENTH_TYPE -> {
                val seventh = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                LeaderItemViewHolder(seventh)
            }
            EIGHTH_TYPE -> {
                val eighth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
                MemberHeaderViewHolder(eighth)
            }
            else -> {
                val nineth = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
                MemberItemViewHolder(nineth)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        if (item is ProductPromotionItems.Img) {
            holder as MainImgViewHolder
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
                    try {
                        val url = item.webLink
                        if (url.isNullOrEmpty()) {
                            Toast.makeText(context,"유효하지 않은 URL입니다.",Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                     Toast.makeText(context,"URL을 열 수 없습니다.",Toast.LENGTH_SHORT).show()
                    }
                }

                if (item.aosLink?.isNotEmpty() == true) {
                    aos.visibility = View.VISIBLE
                    aos.setOnClickListener {
                        try {
                            val url = item.aosLink
                            var editUrl = url
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                editUrl = url
                            }else{
                                if (url.startsWith("http://")){
                                    editUrl = url.substring(7)
                                }else if (url.startsWith("https://")) {
                                    editUrl = url.substring(8)
                                }
                            }
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$editUrl"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context,"URL을 열 수 없습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }else {
                    aos.visibility = View.INVISIBLE
                }

                if (item.iosLink?.isNotEmpty() == true) {
                    ios.visibility = View.VISIBLE
                    ios.setOnClickListener {
                        try {
                            val url = item.iosLink
                            var editUrl = url
                            if (!url.startsWith("http://") || !url.startsWith("https://")) {
                                editUrl = url
                            }else{
                                if (url.startsWith("http://")){
                                    editUrl = url.substring(7)
                                }else if (url.startsWith("https://")) {
                                    editUrl = url.substring(8)
                                }
                            }
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$editUrl"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context,"URL을 열 수 없습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }else {
                    ios.visibility = View.INVISIBLE
                }
            }
        }

        if (item is ProductPromotionItems.ProjectHeader) {
            holder as ProjectHeaderViewHolder
            holder.header.text = getString(R.string.product_header)
        }

        if (item is ProductPromotionItems.ProjectLeaderHeader) {
            holder as LeaderHeaderViewHolder
            holder.header.text = getString(R.string.product_leader)
        }

        if (item is ProductPromotionItems.ProjectLeaderItem) {
            holder as LeaderItemViewHolder
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
                grade.text = items?.getOrNull()?.grade!!.convertGradeFormat().toString()
                level.text = items?.getOrNull()?.level.toString()
                items?.getOrNull()?.level.let { color ->
                    if (color != null) {
                        levelColor.setLevelIcon(color)
                    }
                }
            }
        }

        if (item is ProductPromotionItems.ProjectMemberHeader) {
            holder as MemberHeaderViewHolder
            holder.header.text = getString(R.string.product_member)
        }

        if (item is ProductPromotionItems.ProjectMember) {
            holder as MemberItemViewHolder
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
                    grade.text = member.grade!!.convertGradeFormat().toString()
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

    private fun getString(stringKey : Int) : String {
        return context.getString(stringKey)
    }

    inner class MainImgViewHolder(binding: ItemProductImgBinding) : RecyclerView.ViewHolder(binding.root) {
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

    inner class ProjectHeaderViewHolder(binding: ItemProductProjectHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionHeader
    }

    inner class LeaderHeaderViewHolder(binding: ItemProductMemberHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader
    }

    inner class LeaderItemViewHolder(binding: ItemPostMemberInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
        val lay = binding.layMember
    }

    inner class MemberHeaderViewHolder(binding : ItemProductMemberHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader
    }

    inner class MemberItemViewHolder(binding : ItemPostMemberInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon
        val lay = binding.layMember
    }
}