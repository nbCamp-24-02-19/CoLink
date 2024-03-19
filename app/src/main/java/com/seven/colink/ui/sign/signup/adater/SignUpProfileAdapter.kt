package com.seven.colink.ui.sign.signup.adater

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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
    private val skillAdapter: SkillAdapter,
    private val onPlusSkill: (String) -> Unit,
    private val onClickEnd: (Map<String, Any?>) -> Unit,
) : ListAdapter<SignUpProfileItem, SignUpProfileAdapter.SignUpProfileViewHolder>(
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

    private val storage = mutableMapOf<String, Any?>()

    private fun update(key: String, value: Any?) {
        storage[key] = value
    }

    abstract class SignUpProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: SignUpProfileItem)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SignUpProfileItem.Category -> SignUpProfileViewType.CATEGORY
            is SignUpProfileItem.Skill -> SignUpProfileViewType.SKILL
            is SignUpProfileItem.Level -> SignUpProfileViewType.LEVEL
            is SignUpProfileItem.Info -> SignUpProfileViewType.INFO
            is SignUpProfileItem.Blog -> SignUpProfileViewType.BLOG
        }.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (SignUpProfileViewType.from(viewType)) {
            SignUpProfileViewType.CATEGORY -> CategoryViewHolder(
                ItemSignUpCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ) { key, value -> update(key, value) }

            SignUpProfileViewType.SKILL -> SkillViewHolder(
                binding = ItemSignUpSkillBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                skillAdapter = skillAdapter,
                onPlusSkill = onPlusSkill,
            )

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
            item as SignUpProfileItem.Category

            if (item.mainSpecialty.isNullOrEmpty().not()) {
                btSignUpMainCategoryBtn.text = item.mainSpecialty
                btSignUpSubCategoryBtn.text = item.specialty ?: "선택"
                update("mainSpecialty", item.mainSpecialty)
                update("specialty", item.specialty)
            }

            btSignUpMainCategoryBtn.setOnClickListener {
                mainCategory.setDialog(root.context, "대분류") {
                    btSignUpMainCategoryBtn.text = it
                    btSignUpSubCategoryBtn.text = "선택"
                    update("mainSpecialty", it)
                }.show()
            }

            btSignUpSubCategoryBtn.setOnClickListener {
                when (btSignUpMainCategoryBtn.text) {
                    "기획" -> managerCategory
                    "디자인" -> designCategory
                    "프론트엔드 개발" -> frontCategory
                    "백엔드 개발" -> backendCategory
                    "게임 개발" -> gameCategory
                    else -> {
                        emptyList()
                    }
                }.setDialog(root.context, btSignUpMainCategoryBtn.text.toString()) {
                    btSignUpSubCategoryBtn.text = it
                    update("specialty", it)
                }.show()
            }
        }

    }

    class SkillViewHolder(
        private val binding: ItemSignUpSkillBinding,
        private val skillAdapter: SkillAdapter,
        private val onPlusSkill: (String) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            item as SignUpProfileItem.Skill

            rcSignUpSkills.adapter = skillAdapter
            btSignUpSubCategoryBtn.setOnClickListener {
                skillCategory.setDialog(root.context, "사용가능한 언어/툴을 선택해주세요") {
                    btSignUpSubCategoryBtn.text = it
                    onPlusSkill(it)
                }.show()
            }
        }

    }

    class LevelViewHolder(
        private val binding: ItemSignUpLevelBinding,
        private val update: (String, Any?) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            item as SignUpProfileItem.Level

            LevelEnum.fromNum(item.level ?: 1).setLeveL()

            clLevelDia.setOnClickListener {
                binding.root.context.setLevelDialog {
                    it.setLeveL()
                }.show()
            }
        }

        private fun LevelEnum.setLeveL() = with(binding) {
            ivLevelDiaIcon.setLevelIcon(num ?: 0)
            tvLevelDiaIcon.text = num.toString()
            tvLevelDiaTitle.setText(title ?: LevelEnum.UNKNOWN.title!!)
            tvLevelDiaInfo.setText(info ?: LevelEnum.UNKNOWN.info!!)
            update("level", num)
        }

    }

    class InfoViewHolder(
        private val binding: ItemSignUpInfoBinding,
        private val update: (String, Any?) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            item as SignUpProfileItem.Info

            if (item.info.isNullOrEmpty().not()) {
                etSignUpInfo.setText(item.info)
                update("info", item.info)
            }

            etSignUpInfo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    Unit

                override fun afterTextChanged(s: Editable?) {
                    update("info", s?.toString().takeIf { it?.isNotEmpty() == true })
                }
            })
        }
    }

    class BlogViewHolder(
        private val binding: ItemSignUpBlogBinding,
        private val update: (String, Any?) -> Unit,
        private val storage: Map<String, Any?>,
        private val onClickEnd: (Map<String, Any?>) -> Unit,
    ) : SignUpProfileViewHolder(binding.root) {
        override fun onBind(item: SignUpProfileItem) = with(binding) {
            item as SignUpProfileItem.Blog

            etSignUpGit.setText(item.git)
            etSignUpBlog.setText(item.blog)
            etSignUpLink.setText(item.link)

            btSignUpEnd.setOnClickListener {
                update("git", etSignUpGit.text.takeIf { it.isNotEmpty() }?.toString())
                update("blog", etSignUpBlog.text.takeIf { it.isNotEmpty() }?.toString())
                update("link", etSignUpLink.text.takeIf { it.isNotEmpty() }?.toString())
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
