package com.seven.colink.ui.promotion.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemProductDesImgBinding
import com.seven.colink.databinding.ItemProductEditLinkBinding
import com.seven.colink.databinding.ItemProductEditTitleBinding
import com.seven.colink.databinding.ItemProductImgBinding
import com.seven.colink.databinding.ItemProductMemberHeaderBinding
import com.seven.colink.databinding.ItemProductProjectHeaderBinding
import com.seven.colink.domain.entity.TempProductEntity
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.util.openGallery
import com.seven.colink.util.setLevelIcon

class ProductPromotionEditAdapter (private val mContext: Context,private val recyclerView: RecyclerView, private val dItem : MutableList<ProductPromotionItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {
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

    private fun getString(stringKey : Int) : String {
        return mContext.getString(stringKey)
    }

    fun editTextViewAllFocusOut() {  //포커스 아웃 시키기
        for (i in 0 until itemCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
            if (viewHolder is TitleViewHolder) {
                viewHolder.editDes.clearFocus()
                viewHolder.editTitle.clearFocus()
            }else if (viewHolder is LinkViewHolder) {
                viewHolder.editAosLink.clearFocus()
                viewHolder.editIosLink.clearFocus()
                viewHolder.editWebLink.clearFocus()
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

    private fun imgClickListener(listener:(ViewHolder) -> Unit) {
        this.OnClickImgListener = listener
    }

    fun updateTempData(position: Int, temp: TempProductEntity) {
        tempEntity = temp
        recyclerView.post {
            notifyItemChanged(position)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    fun initResult(galleryResultLauncher1: ActivityResultLauncher<Intent>,
                   galleryResultLauncher2: ActivityResultLauncher<Intent>) {
        imgClickListener { viewHolder ->
            when(viewHolder) {
                is TopMainImgViewHolder -> {
                    openGallery(galleryResultLauncher1)
                }
                is DesImgViewHolder -> {
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
                tempEntity.mainImg = item.img
                mainImg.load(item.img)
            }
        }
    }

    inner class TitleViewHolder(binding : ItemProductEditTitleBinding) : ViewHolder(binding.root) {
        val editTitle = binding.etProductNewTitle
        val editTeam = binding.etProductNewTeam
        val editDes = binding.etDescription

        private fun saveData(position: Int) {
            val title = editTitle.text.toString()
            val des = editDes.text.toString()
            val team = editTeam.text.toString()

            if (tempEntity.title != title || tempEntity.des != des || tempEntity.team != team) {
                tempEntity.title = title
                tempEntity.des = des
                tempEntity.team = team
                updateTempData(position,tempEntity)
            }
        }

        init {
            editTitle.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                    hideKeyboard(editTitle)
                }
            }

            editDes.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                    hideKeyboard(editDes)
                }
            }

            editTeam.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null) {
                        if (s.length >= 9) {
                            editTeam.setError(getString(R.string.input_team_length))
                        }else{
                            editTeam.setError(null)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            editTeam.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                    hideKeyboard(editTeam)
                }
            }

            editTitle.setText(tempEntity.title)
            editDes.setText(tempEntity.des)
            editTeam.setText(tempEntity.team)
        }

        fun bind(item: ProductPromotionItems.Title) {

            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (tempEntity.title.isNullOrEmpty()) {
                    editTitle.setText(item.title)
                    tempEntity.title = item.title
                }else{
                    editTitle.setText(tempEntity.title)
                }
                if (tempEntity.des.isNullOrEmpty()) {
                    editDes.setText(item.des)
                    tempEntity.des = item.des
                }else{
                    editDes.setText(tempEntity.des)
                }
                if (tempEntity.team.isNullOrEmpty()){
                    editTeam.setText(item.team)
                    tempEntity.team = item.team
                }else{
                    editTeam.setText(tempEntity.team)
                }
            }
        }
    }

    inner class DesImgViewHolder(binding: ItemProductDesImgBinding) : ViewHolder(binding.root) {
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

    inner class LinkViewHolder(binding: ItemProductEditLinkBinding) : ViewHolder(binding.root) {
        val editWebLink = binding.etProductWebLink
        val editAosLink = binding.etProductAosLink
        val editIosLink = binding.etProductIosLink

        private fun saveData(position: Int) {
            val web = editWebLink.text.toString()
            val aos = editAosLink.text.toString()
            val ios = editIosLink.text.toString()

            if (tempEntity.web != web || tempEntity.aos != aos || tempEntity.ios != ios) {
                tempEntity.web = web
                tempEntity.aos = aos
                tempEntity.ios = ios
                updateTempData(position,tempEntity)
            }
        }

        init {
            editWebLink.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                    hideKeyboard(editWebLink)
                }
            }

            editAosLink.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                    hideKeyboard(editAosLink)
                }
            }

            editIosLink.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    saveData(adapterPosition)
                    hideKeyboard(editIosLink)
                }
            }
            editAosLink.setText(tempEntity.aos)
            editWebLink.setText(tempEntity.web)
            editIosLink.setText(tempEntity.ios)

        }

        fun bind (item: ProductPromotionItems.Link) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (tempEntity.web.isNullOrEmpty()) {
                    editWebLink.setText(item.webLink)
                    tempEntity.web = item.webLink
                }else{
                    editWebLink.setText(tempEntity.web)
                }
                if (tempEntity.aos.isNullOrEmpty()) {
                    editAosLink.setText(item.aosLink)
                    tempEntity.aos = item.aosLink
                }else{
                    editAosLink.setText(tempEntity.aos)
                }
                if (tempEntity.ios.isNullOrEmpty()) {
                    editIosLink.setText(item.iosLink)
                    tempEntity.ios = item.iosLink
                }else{
                    editIosLink.setText(tempEntity.ios)
                }
            }
        }
    }

    inner class ProjectHeaderViewHolder(binding: ItemProductProjectHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionHeader

        fun bind(item: ProductPromotionItems.ProjectHeader) {
            header.text = getString(R.string.product_header)
        }
    }

    inner class LeaderHeaderViewHolder(binding: ItemProductMemberHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader

        fun bind(item: ProductPromotionItems.ProjectLeaderHeader) {
            header.text = getString(R.string.product_leader)
        }
    }

    inner class LeaderItemViewHolder(binding: ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
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

    inner class MemberHeaderViewHolder(binding : ItemProductMemberHeaderBinding) : ViewHolder(binding.root) {
        val header = binding.tvProductPromotionLeaderHeader

        fun bind(item: ProductPromotionItems.ProjectMemberHeader) {
            header.text = getString(R.string.product_member)
        }
    }

    inner class MemberItemViewHolder(binding : ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
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
                val second = ItemProductEditTitleBinding.inflate(inflater, parent, false)
                TitleViewHolder(second)
            }

            THIRD_TYPE -> {
                val third = ItemProductDesImgBinding.inflate(inflater, parent, false)
                DesImgViewHolder(third)
            }

            FORTH_TYPE -> {
                val forth = ItemProductEditLinkBinding.inflate(inflater, parent, false)
                LinkViewHolder(forth)
            }

            FIFTH_TYPE -> {
                val fifth = ItemProductProjectHeaderBinding.inflate(inflater, parent, false)
                ProjectHeaderViewHolder(fifth)
            }

            SIXTH_TYPE -> {
                val sixth = ItemProductMemberHeaderBinding.inflate(inflater, parent, false)
                LeaderHeaderViewHolder(sixth)
            }

            SEVENTH_TYPE -> {
                val seventh = ItemPostMemberInfoBinding.inflate(inflater, parent, false)
                LeaderItemViewHolder(seventh)
            }

            EIGHTH_TYPE -> {
                val eighth = ItemProductMemberHeaderBinding.inflate(inflater, parent, false)
                MemberHeaderViewHolder(eighth)
            }

            else -> {
                val nineth = ItemPostMemberInfoBinding.inflate(inflater, parent, false)
                MemberItemViewHolder(nineth)
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
            is TitleViewHolder -> {
                item as ProductPromotionItems.Title
                holder.bind(item)
            }
            is DesImgViewHolder -> {
                item as ProductPromotionItems.MiddleImg
                holder.bind(item)
            }
            is LinkViewHolder -> {
                item as ProductPromotionItems.Link
                holder.bind(item)
            }
            is ProjectHeaderViewHolder -> {
                item as ProductPromotionItems.ProjectHeader
                holder.bind(item)
            }
            is LeaderHeaderViewHolder -> {
                item as ProductPromotionItems.ProjectLeaderHeader
                holder.bind(item)
            }
            is LeaderItemViewHolder -> {
                item as ProductPromotionItems.ProjectLeaderItem
                holder.bind(item)
            }
            is MemberHeaderViewHolder -> {
                item as ProductPromotionItems.ProjectMemberHeader
                holder.bind(item)
            }
            is MemberItemViewHolder -> {
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