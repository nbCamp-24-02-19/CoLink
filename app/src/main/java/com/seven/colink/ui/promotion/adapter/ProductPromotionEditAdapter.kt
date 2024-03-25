package com.seven.colink.ui.promotion.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemProductImgBinding
import com.seven.colink.databinding.ItemProductLinkBinding
import com.seven.colink.databinding.ItemProductMemberHeaderBinding
import com.seven.colink.databinding.ItemProductProjectHeaderBinding
import com.seven.colink.databinding.ItemProductTitleBinding
import com.seven.colink.domain.entity.TempProductEntity
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.util.openGallery
import com.seven.colink.util.setLevelIcon

class ProductPromotionEditAdapter (private val recyclerView: RecyclerView, private val dItem : MutableList<ProductPromotionItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {
    interface ItemClick {
        fun onClick(view: View, pos : Int)
    }

    private var OnClickImgListener : ((ViewHolder) -> Unit)? = null

    private var itemClick : ItemClick? = null
    var tempEntity = TempProductEntity()

    private val FIRST_TYPE = 0
    private val SECOND_TYPE = 1
    private val THIRD_TYPE = 2
    private val FORTH_TYPE = 3
    private val FIFTH_TYPE = 4
    private val SIXTH_TYPE = 5
    private val SEVENTH_TYPE = 6
    private val EIGHTH_TYPE = 7
    private val NINETH_TYPE = 8

    fun editTextViewAllFocusOut() {  //포커스 아웃 시키기
        for (i in 0 until itemCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
            if (viewHolder is SecondViewHolder) {
                viewHolder.editDes.clearFocus()
                viewHolder.editTitle.clearFocus()
            }else if (viewHolder is ForthViewHolder) {
                viewHolder.etAppStore.clearFocus()
                viewHolder.etWebLink.clearFocus()
                viewHolder.etPlayStore.clearFocus()
            }
        }
    }

    fun updateImage(selectMainUri: Uri?){
        tempEntity.selectMainImgUri = selectMainUri
        notifyDataSetChanged()
    }

    fun updateDesImage(selectDesUri: Uri?) {
        tempEntity.selectMiddleImgUri = selectDesUri
        notifyDataSetChanged()
    }

    fun imgClickListener(listener:(ViewHolder) -> Unit) {
        this.OnClickImgListener = listener
    }

    fun updateTempData(position: Int, temp: TempProductEntity) {
        tempEntity = temp
        recyclerView.post {
            notifyItemChanged(position)
        }
    }

    fun initResult(galleryResultLauncher1: ActivityResultLauncher<Intent>,
                   galleryResultLauncher2: ActivityResultLauncher<Intent>) {
        imgClickListener { viewHolder ->
            when(viewHolder) {
                is TopMainImgViewHolder -> {
                    openGallery(galleryResultLauncher1)
                }
                is ThirdViewHolder -> {
                    openGallery(galleryResultLauncher2)
                }
            }
        }
    }

    inner class TopMainImgViewHolder(binding: ItemProductImgBinding) : ViewHolder(binding.root) {
        private val mainImg = binding.ivProductPromotion

        init {
            mainImg.setOnClickListener {
                OnClickImgListener?.invoke(this)
            }
        }

        fun bind(item : ProductPromotionItems.Img) {
            if (tempEntity.selectMainImgUri != null) {
                mainImg.load(tempEntity.selectMainImgUri)
            }else{
                mainImg.load(item.img)
            }
        }
    }

    inner class SecondViewHolder(binding : ItemProductTitleBinding) : ViewHolder(binding.root) {
        val editTitle = binding.etProductTitle
        val date = binding.tvProductDate
        val editDes = binding.etProductDes
        val viewTitle = binding.tvProductTitle
        val viewDes = binding.tvProductDes

        private fun saveData(position: Int) {
            val title = editTitle.text.toString()
            val des = editDes.text.toString()

            if (tempEntity.title != title || tempEntity.des != des) {
                tempEntity.title = title
                tempEntity.des = des
                updateTempData(position,tempEntity)
            }
        }

        init {
            editTitle.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                }
            }

            editDes.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                }
            }
            editTitle.setText(tempEntity.title)
            editDes.setText(tempEntity.des)
        }

        fun bind(item: ProductPromotionItems.Title) {
            viewDes.visibility = View.GONE
            viewTitle.visibility = View.GONE
            date.visibility = View.INVISIBLE
            editTitle.visibility = View.VISIBLE
            editDes.visibility = View.VISIBLE

            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (item.title?.isNotEmpty() == true) {
                    editTitle.setText(item.title)
                }else {
                    editTitle.setText(tempEntity.title)
                }

                if (item.des?.isNotEmpty() == true) {
                    editDes.setText(item.des)
                }else {
                    editDes.setText(tempEntity.des)
                }
            }
        }
    }

    inner class ThirdViewHolder(binding: ItemProductImgBinding) : ViewHolder(binding.root) {
        val img = binding.ivProductPromotion
        val lay = binding.layMiddle

        init {
            img.setOnClickListener {
                OnClickImgListener?.invoke(this)
            }
        }

        fun bind(item: ProductPromotionItems.MiddleImg) {
            if (tempEntity.selectMiddleImgUri != null) {
                img.load(tempEntity.selectMiddleImgUri)
            }else{
                img.load(item.img)
            }
        }
    }

    inner class ForthViewHolder(binding : ItemProductLinkBinding) : ViewHolder(binding.root) {
        val viewLink = binding.layProductViewLink
        val viewPlayStore = binding.ivPlaystore
        val viewAppStore = binding.ivAppstore
        val viewWebLink = binding.tvProductWebLink
        val etWebLink = binding.etProductWebLink
        val etPlayStore = binding.etProductPlaystoreLink
        val etAppStore = binding.etProductAppstoreLink

        private fun saveData(position: Int) {
            val web = etWebLink.text.toString()
            val aos = etPlayStore.text.toString()
            val ios = etAppStore.text.toString()

            if (tempEntity.web != web || tempEntity.aos != aos || tempEntity.ios != ios) {
                tempEntity.web = web
                tempEntity.aos = aos
                tempEntity.ios = ios
                updateTempData(position,tempEntity)
            }
        }

        init {
            etWebLink.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                }
            }

            etPlayStore.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                }
            }

            etAppStore.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                }
            }
            etPlayStore.setText(tempEntity.aos)
            etWebLink.setText(tempEntity.web)
            etAppStore.setText(tempEntity.ios)
        }

        fun bind (item: ProductPromotionItems.Link) {
            viewLink.visibility = View.GONE
            viewWebLink.visibility = View.GONE
            etAppStore.visibility = View.VISIBLE
            etPlayStore.visibility = View.VISIBLE
            etWebLink.visibility = View.VISIBLE

            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (item.webLink?.isNotEmpty() == true) {
                    etWebLink.setText(item.webLink)
                    Log.d("item","#eee item = ${item.webLink}")
                }else {
                    etWebLink.setText(tempEntity.web)
                }

                if (item.aosLink?.isNotEmpty() == true) {
                    etPlayStore.setText(item.aosLink)
                }else {
                    etPlayStore.setText(tempEntity.aos)
                }

                if (item.iosLink?.isNotEmpty() == true) {
                    etAppStore.setText(item.iosLink)
                }else{
                    etAppStore.setText(tempEntity.ios)
                }
            }
        }
    }

    inner class FifthViewHolder(binding: ItemProductProjectHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionHeader

        fun bind(item: ProductPromotionItems.ProjectHeader) {
            header.text = "프로젝트멤버"
        }
    }

    inner class SixthViewHolder(binding: ItemProductMemberHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader

        fun bind(item: ProductPromotionItems.ProjectLeaderHeader) {
            header.text = "리더"
        }
    }

    inner class SeventhViewHolder(binding: ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon

        fun bind(item: ProductPromotionItems.ProjectLeaderItem) {
            val items = item.userInfo

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

    inner class EighthViewHolder(binding : ItemProductMemberHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader

        fun bind(item: ProductPromotionItems.ProjectMemberHeader) {
            header.text = "멤버"
        }
    }

    inner class NinethViewHolder(binding : ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
        val img = binding.ivUser
        val name = binding.tvUserName
        val intro = binding.tvUserIntroduction
        val grade = binding.tvUserGrade
        val levelColor = binding.ivLevelDiaIcon
        val level = binding.tvLevelDiaIcon

        fun bind(item: ProductPromotionItems.ProjectMember) {
            val items = item.userInfo
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            FIRST_TYPE -> {
                val first = ItemProductImgBinding.inflate(inflater, parent, false)
                TopMainImgViewHolder(first)
            }

            SECOND_TYPE -> {
                val second = ItemProductTitleBinding.inflate(inflater, parent, false)
                SecondViewHolder(second)
            }

            THIRD_TYPE -> {
                val third = ItemProductImgBinding.inflate(inflater, parent, false)
                ThirdViewHolder(third)
            }

            FORTH_TYPE -> {
                val forth = ItemProductLinkBinding.inflate(inflater, parent, false)
                ForthViewHolder(forth)
            }

            FIFTH_TYPE -> {
                val fifth = ItemProductProjectHeaderBinding.inflate(inflater, parent, false)
                FifthViewHolder(fifth)
            }

            SIXTH_TYPE -> {
                val sixth = ItemProductMemberHeaderBinding.inflate(inflater, parent, false)
                SixthViewHolder(sixth)
            }

            SEVENTH_TYPE -> {
                val seventh = ItemPostMemberInfoBinding.inflate(inflater, parent, false)
                SeventhViewHolder(seventh)
            }

            EIGHTH_TYPE -> {
                val eighth = ItemProductMemberHeaderBinding.inflate(inflater, parent, false)
                EighthViewHolder(eighth)
            }

            else -> {
                val nineth = ItemPostMemberInfoBinding.inflate(inflater, parent, false)
                NinethViewHolder(nineth)
            }
        }
    }

    override fun getItemCount(): Int = dItem.size

    override fun getItemViewType(position: Int): Int {
        return when(dItem[position]) {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dItem[position]

        holder.itemView.setOnClickListener {
            itemClick?.onClick(it,position)
        }

        when (holder) {
            is TopMainImgViewHolder -> {
                item as ProductPromotionItems.Img
                holder.bind(item)
            }
            is SecondViewHolder -> {
                item as ProductPromotionItems.Title
                holder.bind(item)
            }
            is ThirdViewHolder -> {
                item as ProductPromotionItems.MiddleImg
                holder.bind(item)
            }
            is ForthViewHolder -> {
                item as ProductPromotionItems.Link
                holder.bind(item)
            }
            is FifthViewHolder -> {
                item as ProductPromotionItems.ProjectHeader
                holder.bind(item)
            }
            is SixthViewHolder -> {
                item as ProductPromotionItems.ProjectLeaderHeader
                holder.bind(item)
            }
            is SeventhViewHolder -> {
                item as ProductPromotionItems.ProjectLeaderItem
                holder.bind(item)
            }
            is EighthViewHolder -> {
                item as ProductPromotionItems.ProjectMemberHeader
                holder.bind(item)
            }
            is NinethViewHolder -> {
                item as ProductPromotionItems.ProjectMember
                holder.bind(item)
            }
        }
    }

    fun setLeader(leader : ProductPromotionItems.ProjectLeaderItem) {
        val items = dItem.indexOfFirst { it is ProductPromotionItems.ProjectLeaderItem }
        if (items != -1) {
            dItem[items] = leader
            notifyItemChanged(items)
        }else {
            dItem.add(leader)
            notifyItemInserted(dItem.size - 1)
        }
    }

    fun setMember(member : MutableList<ProductPromotionItems.ProjectMember>) {
        val insert = dItem.indexOfFirst { it is ProductPromotionItems.ProjectMember }
        val memberCount = dItem.count{it is ProductPromotionItems.ProjectMember}
        if (memberCount > 0) {
            dItem.removeAll{it is ProductPromotionItems.ProjectMember}
            notifyItemRangeRemoved(insert,memberCount)
        }
        dItem.addAll(insert,member)
        notifyItemRangeInserted(insert, member.size)
    }

    fun getTempData() : TempProductEntity {
        return tempEntity
    }
}