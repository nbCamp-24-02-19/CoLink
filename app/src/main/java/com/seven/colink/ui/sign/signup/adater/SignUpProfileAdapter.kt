package com.seven.colink.ui.sign.signup.adater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ItemSignUpBlogBinding
import com.seven.colink.databinding.ItemSignUpCategoryBinding
import com.seven.colink.databinding.ItemSignUpInfoBinding
import com.seven.colink.databinding.ItemSignUpLevelBinding
import com.seven.colink.databinding.ItemSignUpSkillBinding
import com.seven.colink.databinding.UnknownItemBinding
import com.seven.colink.ui.sign.signup.model.SignUpProfileItem
import com.seven.colink.ui.sign.signup.type.SignUpProfileViewType
import com.seven.colink.util.backendCategory
import com.seven.colink.util.designCategory
import com.seven.colink.util.dialog.enum.LevelEnum
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.dialog.setLevelDialog
import com.seven.colink.util.frontCategory
import com.seven.colink.util.gameCategory
import com.seven.colink.util.mainCategory
import com.seven.colink.util.managerCategory
import com.seven.colink.util.setLevelIcon
import com.seven.colink.util.skillCategory

class SignUpProfileAdapter(
    private val onClickEnd: (Map<String, Any?>) -> Unit
): ListAdapter<SignUpProfileItem, SignUpProfileAdapter.SignUpProfileViewHolder>(
    object : DiffUtil.ItemCallback<SignUpProfileItem>() {
        override fun areItemsTheSame(
            oldItem: SignUpProfileItem,
            newItem: SignUpProfileItem
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: SignUpProfileItem,
            newItem: SignUpProfileItem
        ): Boolean = oldItem == newItem

    }
) {

    private val storage = mutableMapOf<String,Any?>()

    private fun update(key: String, value: Any?) {
        storage[key] = value
    }
    abstract class SignUpProfileViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: SignUpProfileItem)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)){
            is SignUpProfileItem.Category -> SignUpProfileViewType.CATEGORY
            is SignUpProfileItem.Skill -> SignUpProfileViewType.SKILL
            is SignUpProfileItem.Level -> SignUpProfileViewType.LEVEL
            is SignUpProfileItem.Info -> SignUpProfileViewType.INFO
            is SignUpProfileItem.Blog -> SignUpProfileViewType.BLOG
        }.ordinal
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(SignUpProfileViewType.from(viewType)){
        SignUpProfileViewType.CATEGORY -> CategoryViewHolder(
            ItemSignUpCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) { key, value -> update(key, value) }

        SignUpProfileViewType.SKILL -> SkillViewHolder(
            ItemSignUpSkillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) { key, value -> update(key, value) }
        SignUpProfileViewType.LEVEL -> LevelViewHolder(
            ItemSignUpLevelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) { key, value -> update(key, value) }
        SignUpProfileViewType.INFO -> InfoViewHolder(
            ItemSignUpInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) { key, value -> update(key, value) }
        SignUpProfileViewType.BLOG -> BlogViewHolder(
            ItemSignUpBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            update = { key, value -> update(key, value) },
            storage = storage,
            onClickEnd = onClickEnd,
        )

        else -> UnknownViewHolder(
            UnknownItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class CategoryViewHolder(
        private val binding: ItemSignUpCategoryBinding,
        private val update: (String, Any?) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            btSignUpMainCategoryBtn.setOnClickListener {
                mainCategory.setDialog(root.context, "대분류"){
                    btSignUpMainCategoryBtn.text = it
                    btSignUpSubCategoryBtn.text = "선택"
                    update("mainSpecialty", it)
                }.show()
            }

            btSignUpSubCategoryBtn.setOnClickListener {
                when(btSignUpMainCategoryBtn.text) {
                    "기획" -> managerCategory
                    "디자인" -> designCategory
                    "프론트엔드 개발" -> frontCategory
                    "백엔드 개발" -> backendCategory
                    "게임 개발" -> gameCategory
                    else -> { emptyList() }
                }.setDialog(root.context, btSignUpMainCategoryBtn.text.toString()) {
                    btSignUpSubCategoryBtn.text = it
                    update("specialty", it)
                }.show()
            }
        }

    }

    class SkillViewHolder(
        private val binding: ItemSignUpSkillBinding,
        private val update: (String, Any?) -> Unit,
        /*private val skillAdapter: SkillAdapter,*/
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            btSignUpSubCategoryBtn.setOnClickListener {
                skillCategory.setDialog(root.context, "사용가능한 언어/툴을 선택해주세요"){
                    btSignUpSubCategoryBtn.text = it
                }.show()
            }

//            rcSignUpSkills.adapter = skillAdapter
        }

    }

    class LevelViewHolder(
        private val binding: ItemSignUpLevelBinding,
        private val update: (String, Any?) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            clLevelDia.setOnClickListener {
                binding.root.context.setLevelDialog {
                    ivLevelDiaIcon.setLevelIcon(it.num?: 0)
                    tvLevelDiaIcon.text = it.num.toString()
                    tvLevelDiaTitle.setText(it.title?: LevelEnum.UNKNOWN.title!!)
                    tvLevelDiaInfo.setText(it.info?: LevelEnum.UNKNOWN.info!!)
                    update("level", it.num)
                }.show()
            }
        }

    }

    class InfoViewHolder(
        private val binding: ItemSignUpInfoBinding,
        private val update: (String, Any?) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            etSignUpInfo.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus.not()){
                    update("info", etSignUpInfo.text)
                }
            }
        }

    }

    class BlogViewHolder(
        private val binding: ItemSignUpBlogBinding,
        private val update: (String, Any?) -> Unit,
        private val storage: Map<String, Any?>,
        private val onClickEnd: (Map<String, Any?>) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            btSignUpEnd.setOnClickListener {
                onClickEnd(storage)
            }
        }

    }

    class UnknownViewHolder(
        private val binding: UnknownItemBinding
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {

        }

    }

    override fun onBindViewHolder(holder: SignUpProfileViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }


}
