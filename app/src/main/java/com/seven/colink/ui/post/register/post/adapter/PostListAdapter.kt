package com.seven.colink.ui.post.register.post.adapter

import android.content.Context
import com.seven.colink.util.status.PostContentViewTypeItem
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
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
    private val context: Context,
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
                context,
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
                context,
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
                            context,
                            ItemPostRecruitTypeBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false
                            ),
                            onClickView,
                            recruitAdapterOnClickItem
                        )

                        GroupType.STUDY -> StudyItemViewHolder(
                            context,
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
                context,
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
        private val context: Context,
        private val binding: ItemPostEditBinding,
        private val onChangedFocus: (Int, String, String, PostListItem) -> Unit,
        private val onClickView: (View, PostListItem) -> Unit,
        private val onGroupImageClick: (String) -> Unit,
        private val tagAdapterOnClickItem: (Int, TagListItem) -> Unit
    ) : PostViewHolder(binding.root) {
        private var currentItem: PostListItem? = null
        private val tagAdapter = TagListAdapter { item -> tagAdapterOnClickItem(adapterPosition, item) }

        init {
            binding.recyclerViewTags.adapter = tagAdapter
            initializeEditorActionListener()
            initializeFocusChangeListeners()
        }

        private fun initializeEditorActionListener() {
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

        private fun initializeFocusChangeListeners() {
            binding.etTitle.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    notifyTextChange(
                        binding.etTitle.text.toString(),
                        binding.etDescription.text.toString(),
                        currentItem
                    )
                }
            }
            binding.etDescription.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
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

                    if (item.groupType == GroupType.PROJECT) {
                        binding.tvTitle.text = context.getString(R.string.title_project_name)
                        binding.tvProjectDescription.text =
                            context.getString(R.string.title_project_description)
                    } else {
                        binding.tvTitle.text = context.getString(R.string.title_study_name)
                        binding.tvProjectDescription.text =
                            context.getString(R.string.title_study_description)
                    }

                    val tagList = item.tags?.map { TagListItem.Item(it) } ?: emptyList()
                    tagAdapter.submitList(tagList)
                    binding.ivPostImage.setOnClickListener { onClickView(it, item) }
                }
            }
        }

        private fun notifyTextChange(title: String, description: String, item: PostListItem?) {
            item?.let { onChangedFocus(adapterPosition, title, description, it) }
        }
    }

    class PostOptionItemViewHolder(
        private val context: Context,
        private val binding: ItemPostSelectionTypeBinding,
        private val onChangedFocus: (Int, String, String, PostListItem) -> Unit
    ) : PostViewHolder(binding.root) {
        private var currentItem: PostListItem? = null
        private var currentPosition: Int = RecyclerView.NO_POSITION

        init {
            initializeFocusChangeListeners()
        }

        private fun initializeFocusChangeListeners() {
            binding.tvPrecautions.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    notifyTextChange(
                        binding.tvPrecautions.text.toString(),
                        binding.tvDescription.text.toString(),
                        currentItem
                    )
                }
            }
            binding.tvDescription.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    notifyTextChange(
                        binding.tvPrecautions.text.toString(),
                        binding.tvDescription.text.toString(),
                        currentItem
                    )
                }
            }
        }

        override fun onBind(item: PostListItem) {
            if (item is PostListItem.PostOptionItem) {
                currentItem = item
                binding.tvPrecautions.setText(item.precautions)
                binding.tvDescription.setText(item.recruitInfo)
                currentPosition = adapterPosition
            }
        }

        private fun notifyTextChange(precautions: String, description: String, item: PostListItem?) {
            item?.let { onChangedFocus(adapterPosition, precautions, description, it) }
        }
    }


    class ProjectItemViewHolder(
        private val context: Context,
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
                val maxPersonnel = item.recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0
                val mainColor = ContextCompat.getColor(context, R.color.main_color)
                val coloredText = highlightNumbers(
                    context.getString(R.string.total_personnel, maxPersonnel),
                    mainColor
                )
                binding.tvTotalRecruit.setText(coloredText, TextView.BufferType.SPANNABLE)
                val recruitList = item.recruit
                recruitAdapter.submitList(recruitList)
                binding.ivAddRecruit.setOnClickListener {

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
            }
        }
    }


    class StudyItemViewHolder(
        private val context: Context,
        private val binding: ItemPersonnelComponentBinding,
        private val onClickView: (View, PostListItem) -> Unit
    ) :
        PostViewHolder(binding.root) {
        override fun onBind(item: PostListItem) {
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
        private val context: Context,
        private val binding: ItemPostRecruitTitleBinding,
    ) :
        PostViewHolder(binding.root) {
        override fun onBind(item: PostListItem) {
            if (item is PostListItem.TitleItem) {
                binding.tvTitle.text = context.getString(item.message1!!)
                binding.tvSubTitle.text = item.message2?.let {
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

