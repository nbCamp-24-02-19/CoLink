package com.seven.colink.ui.post.register.post.adapter

import com.seven.colink.util.status.PostContentViewTypeItem
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ItemPersonnelComponentBinding
import com.seven.colink.databinding.ItemPostCompleteButtonBinding
import com.seven.colink.databinding.ItemPostEditBinding
import com.seven.colink.databinding.ItemPostRecruitTitleBinding
import com.seven.colink.databinding.ItemPostRecruitTypeBinding
import com.seven.colink.databinding.ItemPostSelectionTypeBinding
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.post.register.post.model.PostListItem
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.applyDarkFilter
import com.seven.colink.util.highlightNumbers
import com.seven.colink.util.showToast
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostContentViewType

class PostListAdapter(
    private val onChangedFocus: (Int, String, String, PostListItem) -> Unit,
    private val onClickView: (View, PostListItem) -> Unit,
    private val onGroupImageClick: (String) -> Unit,
    private val tagAdapterOnClickItem: (Int, TagListItem) -> Unit,
    private val recruitAdapterOnClickItem: (Int, RecruitInfo) -> Unit
) : ListAdapter<PostListItem, PostListAdapter.PostViewHolder>(
    object : DiffUtil.ItemCallback<PostListItem>() {
        override fun areItemsTheSame(
            oldItem: PostListItem,
            newItem: PostListItem
        ): Boolean =
            when {
                oldItem is PostListItem.PostItem && newItem is PostListItem.PostItem -> {
                    oldItem.key == newItem.key
                }

                oldItem is PostListItem.PostOptionItem && newItem is PostListItem.PostOptionItem -> {
                    oldItem.key == newItem.key
                }

                oldItem is PostListItem.RecruitItem && newItem is PostListItem.RecruitItem -> {
                    oldItem.key == newItem.key
                }

                else -> {
                    oldItem == newItem
                }

            }

        override fun areContentsTheSame(
            oldItem: PostListItem,
            newItem: PostListItem
        ): Boolean = oldItem == newItem

    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder =
        when (PostContentViewType.from(viewType)) {
            PostContentViewType.ITEM -> PostItemViewHolder(
                ItemPostEditBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onChangedFocus,
                onClickView,
                onGroupImageClick,
                tagAdapterOnClickItem
            )

            PostContentViewType.OPTION_ITEM -> PostOptionItemViewHolder(
                ItemPostSelectionTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onChangedFocus
            )

            PostContentViewType.GROUP_TYPE -> {
                val recruitItem =
                    currentList.firstOrNull { it is PostListItem.RecruitItem } as? PostListItem.RecruitItem
                if (recruitItem != null) {
                    when (recruitItem.groupType) {
                        GroupType.PROJECT -> ProjectItemViewHolder(
                            ItemPostRecruitTypeBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            ),
                            onClickView,
                            recruitAdapterOnClickItem
                        )

                        GroupType.STUDY -> StudyItemViewHolder(
                            ItemPersonnelComponentBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            ),
                            onClickView
                        )

                        else -> throw IllegalArgumentException("Invalid groupType")
                    }
                } else {
                    throw IllegalStateException("No RecruitItem found in the list")
                }
            }

            PostContentViewType.TITLE -> TitleItemViewHolder(
                ItemPostRecruitTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            PostContentViewType.BUTTON_COMPLETE -> ButtonItemViewHolder(
                ItemPostCompleteButtonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onClickView,
            )

            else -> throw IllegalArgumentException("Invalid viewType")

        }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.onBind(item)
        }
    }

    abstract class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: PostListItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PostListItem.PostItem -> PostContentViewTypeItem.ITEM
        is PostListItem.PostOptionItem -> PostContentViewType.OPTION_ITEM
        is PostListItem.RecruitItem -> PostContentViewType.GROUP_TYPE
        is PostListItem.TitleItem -> PostContentViewType.TITLE
        is PostListItem.ButtonItem -> PostContentViewType.BUTTON_COMPLETE
        else -> GroupContentViewType.UNKNOWN
    }.ordinal


    class PostItemViewHolder(
        private val binding: ItemPostEditBinding,
        private val onChangedFocus: (Int, String, String, PostListItem) -> Unit,
        private val onClickView: (View, PostListItem) -> Unit,
        private val onGroupImageClick: (String) -> Unit,
        private val tagAdapterOnClickItem: (Int, TagListItem) -> Unit
    ) : PostViewHolder(binding.root) {
        private var currentItem: PostListItem? = null
        private val tagAdapter =
            TagListAdapter { item -> tagAdapterOnClickItem(adapterPosition, item) }
        private val editTexts
            get() = with(binding) {
                listOf(
                    etTitle,
                    etDescription
                )
            }

        init {
            setupTagAdapter()
            setOnEditorActionListener()
            setTextChangeListener()
        }

        private fun setupTagAdapter() {
            binding.recyclerViewTags.adapter = tagAdapter
        }

        private fun setOnEditorActionListener() {
            binding.etGroupTag.setOnEditorActionListener { _, actionId, event ->
                if ((actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
                    && binding.etGroupTag.text.toString().trim().isNotBlank()
                ) {
                    onGroupImageClick(binding.etGroupTag.text.toString().trim())
                    binding.etGroupTag.text.clear()
                    return@setOnEditorActionListener true
                }
                false
            }
        }

        private fun setTextChangeListener() {
            editTexts.forEach { editText ->
                editText.addTextChangedListener {
                    notifyTextChange(
                        binding.etTitle.text.toString(),
                        binding.etDescription.text.toString(),
                        currentItem
                    )
                }
            }
        }

        private var boolean = true
        override fun onBind(item: PostListItem) {
            val context = binding.root.context
            if (item is PostListItem.PostItem) {
                currentItem = item
                with(binding) {
                    if (boolean) {
                        etTitle.setText(item.title)
                        etDescription.setText(item.description)
                        boolean = false
                    }
                    tvProjectDescriptionInfo.text = item.descriptionMessage
                    ivPostImage.load(item.selectedImageUrl ?: item.imageUrl)
                    ivPostImageBg.load(item.selectedImageUrl ?: item.imageUrl)

                    if (item.selectedImageUrl != null) {
                        binding.ivPostImageBg.applyDarkFilter()
                    }

                    val titleRes =
                        if (item.groupType == GroupType.PROJECT) R.string.title_project_name else R.string.title_study_name
                    val descriptionRes =
                        if (item.groupType == GroupType.PROJECT) R.string.title_project_description else R.string.title_study_description

                    tvTitle.text = context.getString(titleRes)
                    tvProjectDescription.text = context.getString(descriptionRes)

                    val tagList = item.tags?.map { TagListItem.Item(it) } ?: emptyList()
                    tagAdapter.submitList(tagList)
                    ivPostImage.setOnClickListener { onClickView(it, item) }
                }
            }
        }

        private fun notifyTextChange(title: String, description: String, item: PostListItem?) {
            item?.let { onChangedFocus(adapterPosition, title, description, it) }
        }
    }

    class PostOptionItemViewHolder(
        private val binding: ItemPostSelectionTypeBinding,
        private val onChangedFocus: (Int, String, String, PostListItem) -> Unit
    ) : PostViewHolder(binding.root) {
        private var currentItem: PostListItem? = null
        private val editTexts
            get() = with(binding) {
                listOf(
                    etPrecautions,
                    etRecruitInfo
                )
            }

        init {
            setTextChangeListener()
        }

        private fun setTextChangeListener() {
            editTexts.forEach { editText ->
                editText.addTextChangedListener {
                    notifyTextChange(
                        binding.etPrecautions.text.toString(),
                        binding.etRecruitInfo.text.toString(),
                        currentItem
                    )
                }
            }
        }

        override fun onBind(item: PostListItem) {
            if (item is PostListItem.PostOptionItem) {
                currentItem = item
                binding.etPrecautions.setText(item.precautions)
                binding.etRecruitInfo.setText(item.recruitInfo)
                binding.tvTitleAsterisk.isVisible = true
                binding.tvDescriptionAsterisk.isVisible = true
            }
        }

        private fun notifyTextChange(
            precautions: String,
            description: String,
            item: PostListItem?
        ) {
            item?.let { onChangedFocus(adapterPosition, precautions, description, it) }
        }
    }


    class ProjectItemViewHolder(
        private val binding: ItemPostRecruitTypeBinding,
        private val onClickView: (View, PostListItem) -> Unit,
        private val recruitAdapterOnClickItem: (Int, RecruitInfo) -> Unit
    ) :
        PostViewHolder(binding.root) {
        private val recruitAdapter = RecruitListAdapter { item ->
            recruitAdapterOnClickItem(adapterPosition, item)
        }

        init {
            binding.recyclerViewRecruit.adapter = recruitAdapter
        }

        override fun onBind(item: PostListItem) {
            if (item is PostListItem.RecruitItem) {
                setupRecruitItem(item)
                setAddRecruitClickListener(item)
            }
        }

        private fun setupRecruitItem(item: PostListItem.RecruitItem) {
            val context = binding.root.context
            val maxPersonnel = item.recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0
            val mainColor = ContextCompat.getColor(context, R.color.main_color)
            val coloredText = highlightNumbers(
                context.getString(R.string.total_personnel, maxPersonnel),
                mainColor
            )
            binding.tvTotalRecruit.setText(coloredText, TextView.BufferType.SPANNABLE)

            val recruitList = item.recruit
            recruitAdapter.submitList(recruitList)
        }

        private fun setAddRecruitClickListener(item: PostListItem.RecruitItem) {
            val context = binding.root.context
            binding.ivAddRecruit.setOnClickListener {
                if (item.canAddRecruit()) {
                    onClickView(it, item)
                } else {
                    context.showToast(context.getString(R.string.limited_people, LIMITED_PEOPLE))
                }
            }
        }

        private fun PostListItem.RecruitItem.canAddRecruit(): Boolean {
            val maxPersonnel = recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0
            return maxPersonnel < LIMITED_PEOPLE
        }
    }


    class StudyItemViewHolder(
        private val binding: ItemPersonnelComponentBinding,
        private val onClickView: (View, PostListItem) -> Unit
    ) :
        PostViewHolder(binding.root) {
        override fun onBind(item: PostListItem) {
            val context = binding.root.context
            if (item is PostListItem.RecruitItem) {
                binding.ivMinusPersonnel.setOnClickListener {
                    onClickView(it, item)
                }
                val maxPersonnel = item.recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0
                binding.ivPlusPersonnel.setOnClickListener {
                    if (maxPersonnel < LIMITED_PEOPLE) {
                        onClickView(it, item)
                    } else {
                        context.showToast(
                            context.getString(
                                R.string.limited_people,
                                LIMITED_PEOPLE
                            )
                        )
                    }
                }

                binding.tvRecruitPersonnel.text = maxPersonnel.toString()
            }
        }
    }

    class TitleItemViewHolder(
        private val binding: ItemPostRecruitTitleBinding,
    ) :
        PostViewHolder(binding.root) {
        override fun onBind(item: PostListItem) {
            val context = binding.root.context
            if (item is PostListItem.TitleItem) {
                binding.tvTitle.text = context.getString(item.firstMessage!!)
                binding.tvSubTitle.text = item.secondMessage?.let {
                    context.getString(
                        it,
                        LIMITED_PEOPLE
                    )
                }
            }
        }
    }

    class ButtonItemViewHolder(
        private val binding: ItemPostCompleteButtonBinding,
        private val onClickView: (View, PostListItem) -> Unit
    ) :
        PostViewHolder(binding.root) {
        override fun onBind(item: PostListItem) {
            if (item is PostListItem.ButtonItem) {
                binding.postComplete.setOnClickListener {
                    onClickView(it, item)
                }
            }
        }
    }

}

