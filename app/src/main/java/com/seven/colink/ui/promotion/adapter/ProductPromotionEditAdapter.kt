package com.seven.colink.ui.promotion.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.seven.colink.databinding.ItemPostMemberInfoBinding
import com.seven.colink.databinding.ItemProductImgBinding
import com.seven.colink.databinding.ItemProductLinkBinding
import com.seven.colink.databinding.ItemProductMemberHeaderBinding
import com.seven.colink.databinding.ItemProductProjectHeaderBinding
import com.seven.colink.databinding.ItemProductTitleBinding
import com.seven.colink.ui.promotion.ProductPromotionItems
import com.seven.colink.util.setLevelIcon

//class ProductPromotionEditAdapter() : ListAdapter<ProductPromotionItems, RecyclerView.ViewHolder>(
//    ProductPromotionDiffUtil
//) {
//    object ProductPromotionDiffUtil : DiffUtil.ItemCallback<ProductPromotionItems>() {
//        override fun areItemsTheSame(
//            oldItem: ProductPromotionItems, newItem: ProductPromotionItems
//        ): Boolean {
//            return when {
//                oldItem is ProductPromotionItems.Img && newItem is ProductPromotionItems.Img -> {
//                    oldItem == newItem
//                }
//
//                oldItem is ProductPromotionItems.Title && newItem is ProductPromotionItems.Title -> {
//                    oldItem == newItem
//                }
//
//                oldItem is ProductPromotionItems.ProjectLeaderItem && newItem is ProductPromotionItems.ProjectLeaderItem -> {
//                    oldItem.userInfo?.onSuccess { it?.uid } == newItem.userInfo?.onSuccess { it?.uid }
//                }
//
//                oldItem is ProductPromotionItems.MiddleImg && newItem is ProductPromotionItems.MiddleImg -> {
//                    oldItem.img == newItem.img
//                }
//
//                oldItem is ProductPromotionItems.Link && newItem is ProductPromotionItems.Link -> {
//                    oldItem == newItem
//                }
//
//                oldItem is ProductPromotionItems.ProjectMember && newItem is ProductPromotionItems.ProjectMember -> {
//                    oldItem.userInfo == newItem.userInfo
//                }
//
//                else -> {
//                    oldItem == newItem
//                }
//            }
//        }
//
//        override fun areContentsTheSame(
//            oldItem: ProductPromotionItems, newItem: ProductPromotionItems
//        ): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    interface ItemClick {
//        fun onClick(view: View,position: Int)
//    }
//
//    var itemClick : ItemClick? = null
//
//    private val FIRST_TYPE = 0
//    private val SECOND_TYPE = 1
//    private val THIRD_TYPE = 2
//    private val FORTH_TYPE = 3
//    private val FIFTH_TYPE = 4
//    private val SIXTH_TYPE = 5
//    private val SEVENTH_TYPE = 6
//    private val EIGHTH_TYPE = 7
//    private val NINETH_TYPE = 8
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//
//        return when (viewType) {
//            FIRST_TYPE -> {
//                val first = ItemProductImgBinding.inflate(inflater,parent,false)
//                FirstViewHolder(first)
//            }
//            SECOND_TYPE -> {
//                val second = ItemProductTitleBinding.inflate(inflater,parent,false)
//                SecondViewHolder(second)
//            }
//            THIRD_TYPE -> {
//                val third = ItemProductImgBinding.inflate(inflater,parent,false)
//                ThirdViewHolder(third)
//            }
//            FORTH_TYPE -> {
//                val forth = ItemProductLinkBinding.inflate(inflater,parent,false)
//                ForthViewHolder(forth)
//            }
//            FIFTH_TYPE -> {
//                val fifth = ItemProductProjectHeaderBinding.inflate(inflater,parent,false)
//                FifthViewHolder(fifth)
//            }
//            SIXTH_TYPE -> {
//                val sixth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
//                SixthViewHolder(sixth)
//            }
//            SEVENTH_TYPE -> {
//                val seventh = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
//                SeventhViewHolder(seventh)
//            }
//            EIGHTH_TYPE -> {
//                val eighth = ItemProductMemberHeaderBinding.inflate(inflater,parent,false)
//                EighthViewHolder(eighth)
//            }
//            else -> {
//                val nineth = ItemPostMemberInfoBinding.inflate(inflater,parent,false)
//                NinethViewHolder(nineth)
//            }
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = getItem(position)
//
//        if (item is ProductPromotionItems.Img) {
//            holder as FirstViewHolder
//            holder.img.load(item.img)
//
//            holder.img.setOnClickListener {
//                itemClick?.onClick(it,position)
//            }
//        }
//
//        if (item is ProductPromotionItems.Title) {
//            holder as SecondViewHolder
//            with(holder) {
//                viewDes.visibility = View.GONE
//                viewTitle.visibility = View.GONE
//                date.visibility = View.INVISIBLE
//                editTitle.visibility = View.VISIBLE
//                editDes.visibility = View.VISIBLE
//            }
//        }
//
//        if (item is ProductPromotionItems.MiddleImg) {
//            holder as ThirdViewHolder
//            holder.img.load(item.img)
//
//            holder.img.setOnClickListener {
//                itemClick?.onClick(it,position)
//            }
//        }
//
//        if (item is ProductPromotionItems.Link) {
//            holder as ForthViewHolder
//            with(holder) {
//                viewLink.visibility = View.GONE
//                viewWebLink.visibility = View.GONE
//                etAppStore.visibility = View.VISIBLE
//                etPlayStore.visibility = View.VISIBLE
//                etWebLink.visibility = View.VISIBLE
//            }
//        }
//
//        if (item is ProductPromotionItems.ProjectHeader) {
//            holder as FifthViewHolder
//            holder.header.text = "프로젝트 멤버"
//        }
//
//        if (item is ProductPromotionItems.ProjectLeaderHeader) {
//            holder as SixthViewHolder
//            holder.header.text = "리더"
//        }
//
//        if (item is ProductPromotionItems.ProjectLeaderItem) {
//            holder as SeventhViewHolder
//            val items = item.userInfo
//            with(holder) {
//                img.load(items?.getOrNull()?.photoUrl)
//                Log.d("Adapter","#aaa img = ${items?.getOrNull()?.photoUrl}")
//                name.text = items?.getOrNull()?.name
//                Log.d("Adapter","#aaa name = ${items?.getOrNull()?.name}")
//                Log.d("Adapter","#aaa name2 = ${items?.onSuccess { it?.name }}")
//                intro.text = items?.getOrNull()?.info
//                Log.d("Adapter","#aaa info = ${items?.getOrNull()?.info}")
//                grade.text = items?.getOrNull()?.grade.toString()
//                Log.d("Adapter","#aaa grade = ${items?.getOrNull()?.grade}")
//                level.text = items?.getOrNull()?.level.toString()
//                Log.d("Adapter","#aaa level = ${items?.getOrNull()?.level}")
//                items?.getOrNull()?.level.let { color ->
//                    if (color != null) {
//                        levelColor.setLevelIcon(color)
//                    }
//                }
//
////                img.load(items?.onSuccess { it?.photoUrl })
////                name.text = items?.onSuccess { it?.name }?.toString()
////                intro.text = items?.onSuccess { it?.info }?.toString()
////                grade.text = items?.onSuccess { it?.grade }?.toString()
////                level.text = items?.onSuccess { it?.level }?.toString()
////                items?.onSuccess { it?.level?.let { color -> levelColor.setLevelIcon(color) } }
////                img.load(item.userInfo.photoUrl)
////                name.text = item.userInfo.name
////                intro.text = item.userInfo.info
////                grade.text = item.userInfo.grade?.toString()
////                level.text = item.userInfo.level?.toString()
////                item.userInfo.level?.let { levelColor.setLevelIcon(it) }
//            }
//        }
//
//        if (item is ProductPromotionItems.ProjectMemberHeader) {
//            holder as EighthViewHolder
//            holder.header.text = "멤버"
//        }
//
//        if (item is ProductPromotionItems.ProjectMember) {
//            holder as NinethViewHolder
//            val items = item.userInfo
//            with(holder) {
//                items?.forEach { member ->
//                    img.load(member?.getOrNull()?.photoUrl)
//                    name.text = member?.getOrNull()?.name
//                    intro.text = member?.getOrNull()?.info
//                    grade.text = member?.getOrNull()?.grade.toString()
//                    level.text = member?.getOrNull()?.level.toString()
//                    member?.getOrNull()?.level.let { color ->
//                        if (color != null) {
//                            levelColor.setLevelIcon(color)
//                        }
//                    }
//
//
////                    img.load(member?.onSuccess { it?.photoUrl })
////                    name.text = member?.onSuccess { it?.name }.toString()
////                    intro.text = member?.onSuccess { it?.info }.toString()
////                    grade.text = member?.onSuccess { it?.grade }.toString()
////                    level.text = member?.onSuccess { it?.level }.toString()
////                    member?.onSuccess {
////                        it?.level?.let { color ->
////                        levelColor.setLevelIcon(color)
////                        }
////                    }
//                }
//            }
//
//            with(holder) {
////                item.userInfo.forEach {
////                    img.load(it.photoUrl)
////                    name.text = it.name
////                intro.text = it.info
////                grade.text = it.grade?.toString()
////                level.text = it.level?.toString()
////                it.level?.let { color ->
////                    levelColor.setLevelIcon(color) }
////                }
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = currentList.size
//
//    override fun getItemViewType(position: Int): Int {
//        return when(getItem(position)) {
//            is ProductPromotionItems.Img -> FIRST_TYPE
//            is ProductPromotionItems.Title -> SECOND_TYPE
//            is ProductPromotionItems.MiddleImg -> THIRD_TYPE
//            is ProductPromotionItems.Link -> FORTH_TYPE
//            is ProductPromotionItems.ProjectHeader -> FIFTH_TYPE
//            is ProductPromotionItems.ProjectLeaderHeader -> SIXTH_TYPE
//            is ProductPromotionItems.ProjectLeaderItem -> SEVENTH_TYPE
//            is ProductPromotionItems.ProjectMemberHeader -> EIGHTH_TYPE
//            is ProductPromotionItems.ProjectMember -> NINETH_TYPE
//        }
//    }
//
//    fun setLeader(leader : ProductPromotionItems.ProjectLeaderItem) {
//        val new = currentList.toMutableList()
//        val init = new.indexOfFirst { it is ProductPromotionItems.ProjectLeaderItem }
//        if (init != -1) {
//            new.removeAt(init)
//        }
//        new.add(init,leader)
//        submitList(new)
//    }
//
//    fun setMember(member : List<ProductPromotionItems.ProjectMember>) {
//        val mem = currentList.toMutableList()
//
//
//
//    }
//
//
//    inner class FirstViewHolder(binding: ItemProductImgBinding) : ViewHolder(binding.root) {
//        val img = binding.ivProductPromotion
//    }
//
//    inner class SecondViewHolder(binding : ItemProductTitleBinding) : ViewHolder(binding.root) {
//        val editTitle = binding.etProductTitle
//        val date = binding.tvProductDate
//        val editDes = binding.etProductDes
//        val viewTitle = binding.tvProductTitle
//        val viewDes = binding.tvProductDes
//
//    }
//
//    inner class ThirdViewHolder(binding: ItemProductImgBinding) : ViewHolder(binding.root) {
//        val img = binding.ivProductPromotion
//    }
//
//    inner class ForthViewHolder(binding : ItemProductLinkBinding) : ViewHolder(binding.root) {
//        val viewLink = binding.layProductViewLink
//        val viewPlayStore = binding.ivPlaystore
//        val viewAppStore = binding.ivAppstore
//        val viewWebLink = binding.tvProductWebLink
//        val etWebLink = binding.etProductWebLink
//        val etPlayStore = binding.etProductPlaystoreLink
//        val etAppStore = binding.etProductAppstoreLink
//    }
//
//    inner class FifthViewHolder(binding: ItemProductProjectHeaderBinding) : ViewHolder(binding.root) {
//        val header = binding.tvProductPromotionHeader
//    }
//
//    inner class SixthViewHolder(binding: ItemProductMemberHeaderBinding) : ViewHolder(binding.root) {
//        val header = binding.tvProductPromotionLeaderHeader
//    }
//
//    inner class SeventhViewHolder(binding: ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
//        val img = binding.ivUser
//        val name = binding.tvUserName
//        val intro = binding.tvUserIntroduction
//        val grade = binding.tvUserGrade
//        val levelColor = binding.ivLevelDiaIcon
//        val level = binding.tvLevelDiaIcon
//    }
//
//    inner class EighthViewHolder(binding : ItemProductMemberHeaderBinding) : ViewHolder(binding.root) {
//        val header = binding.tvProductPromotionLeaderHeader
//    }
//
//    inner class NinethViewHolder(binding : ItemPostMemberInfoBinding) : ViewHolder(binding.root) {
//        val img = binding.ivUser
//        val name = binding.tvUserName
//        val intro = binding.tvUserIntroduction
//        val grade = binding.tvUserGrade
//        val levelColor = binding.ivLevelDiaIcon
//        val level = binding.tvLevelDiaIcon
//    }
//}

class ProductPromotionEditAdapter (private val recyclerView: RecyclerView, private val dItem : MutableList<ProductPromotionItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {
    interface ItemClick {
        fun onClick(view: View, pos : Int)
    }

    private var itemClick : ItemClick? = null

    private val FIRST_TYPE = 0
    private val SECOND_TYPE = 1
    private val THIRD_TYPE = 2
    private val FORTH_TYPE = 3
    private val FIFTH_TYPE = 4
    private val SIXTH_TYPE = 5
    private val SEVENTH_TYPE = 6
    private val EIGHTH_TYPE = 7
    private val NINETH_TYPE = 8

    inner class FirstViewHolder(binding: ItemProductImgBinding) : ViewHolder(binding.root) {
        val img = binding.ivProductPromotion

        fun bind(item : ProductPromotionItems.Img) {
            img.load(item.img)
        }
    }

    inner class SecondViewHolder(binding : ItemProductTitleBinding) : ViewHolder(binding.root) {
        val editTitle = binding.etProductTitle
        val date = binding.tvProductDate
        val editDes = binding.etProductDes
        val viewTitle = binding.tvProductTitle
        val viewDes = binding.tvProductDes

        fun bind(item: ProductPromotionItems.Title) {
            viewDes.visibility = View.GONE
            viewTitle.visibility = View.GONE
            date.visibility = View.INVISIBLE
            editTitle.visibility = View.VISIBLE
            editDes.visibility = View.VISIBLE

//            editTitle.setText(item.title)
//            editDes.setText(item.des)
        }
    }

    inner class ThirdViewHolder(binding: ItemProductImgBinding) : ViewHolder(binding.root) {
        val img = binding.ivProductPromotion

        fun bind(item: ProductPromotionItems.MiddleImg) {
            img.load(item.img)
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

        fun bind (item: ProductPromotionItems.Link) {
            viewLink.visibility = View.GONE
            viewWebLink.visibility = View.GONE
            etAppStore.visibility = View.VISIBLE
            etPlayStore.visibility = View.VISIBLE
            etWebLink.visibility = View.VISIBLE
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
            Log.d("Adapter", "#aaa grade = ${items?.getOrNull()?.name}")
            intro.text = items?.getOrNull()?.info
            grade.text = items?.getOrNull()?.grade.toString()
            level.text = items?.getOrNull()?.level.toString()
            Log.d("Adapter", "#aaa level = ${items?.getOrNull()?.level}")
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
//            items?.forEach { member ->
//                img.load(member?.photoUrl)
//                name.text = member?.name
//                intro.text = member?.info
//                grade.text = member?.grade.toString()
//                level.text = member?.level.toString()
//                member?.level.let { color ->
//                    if (color != null) {
//                        levelColor.setLevelIcon(color)
//                    }
//                }
//            }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            FIRST_TYPE -> {
                val first = ItemProductImgBinding.inflate(inflater, parent, false)
                FirstViewHolder(first)
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

        when (holder) {
            is FirstViewHolder -> {
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

    fun getTitleEditText(position: Int) : EditText? {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        return if (viewHolder is SecondViewHolder) {
            viewHolder.editTitle
        }else{
            null
//            val itemView = LayoutInflater.from()
        }
    }

    fun getDesEditText(position: Int) : EditText? {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        return if (viewHolder is SecondViewHolder) {
            viewHolder.editDes
        }else{
            null
        }
    }

    fun getMainImageView(position: Int) : ImageView? {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        return if (viewHolder is FirstViewHolder) {
            viewHolder.img
        }else{
            null
        }
    }

    fun getMiddleImageView(position: Int) : ImageView? {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        return if (viewHolder is ThirdViewHolder) {
            viewHolder.img
        }else{
            null
        }
    }

    fun getWebLink(position: Int) : EditText? {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        return if (viewHolder is ForthViewHolder) {
            viewHolder.etWebLink
        }else{
            null
        }
    }

    fun getAosLink(position: Int) : EditText? {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        return if (viewHolder is ForthViewHolder) {
            viewHolder.etPlayStore
        }else{
            null
        }
    }

    fun getIosLink(position: Int) : EditText? {
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
        return if (viewHolder is ForthViewHolder) {
            viewHolder.etAppStore
        }else{
            null
        }
    }
}