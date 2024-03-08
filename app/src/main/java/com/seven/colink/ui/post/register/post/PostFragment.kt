package com.seven.colink.ui.post.register.post

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.FragmentPostBinding
import com.seven.colink.ui.post.register.post.adapter.RecruitListAdapter
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.TagEvent
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.ui.post.register.post.viewmodel.PostViewModel
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.applyDarkFilter
import com.seven.colink.util.dialog.RecruitDialog
import com.seven.colink.util.highlightNumbers
import com.seven.colink.util.openGallery
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.showToast
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding: FragmentPostBinding get() = _binding!!

    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private val viewModel: PostViewModel by viewModels()
    private val sharedViewModel: PostSharedViewModel by activityViewModels()

    private val tagListAdapter: TagListAdapter by lazy {
        TagListAdapter(onClickItem = { tag ->
            when (tag) {
                is TagListItem.Item -> viewModel.removeTagItem(tag.name)
                else -> Unit
            }
        })
    }

    private val recruitListAdapter: RecruitListAdapter by lazy {
        RecruitListAdapter(onClickItem = { entity ->
            viewModel.removeRecruitInfo(entity.type)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initRegisterForActivityResult()
        initViewModel()
        initSharedViewModel()
    }

    private fun initSharedViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            groupType.collect {
                with(viewModel) {
                    if (it != null) {
                        setGroupType(it)
                        viewByGroupType(it)
                        viewModel.setViewByGroupType()
                    }
                }
            }
        }

        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.setEntity(it)
                    viewModel.setViewByEntity()
                }
            }
        }

        lifecycleScope.launch {
            entryType.collect {
                with(viewModel) {
                    if (it != null) {
                        setEntryType(it)
                    }
                }
            }
        }
    }

    private fun initRegisterForActivityResult() {
        galleryResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.handleGalleryResult(result.resultCode, result.data)
            }
        }
    }

    private fun initView() = with(binding) {
        setEntityTypeView()
        setListAdapter()

        etGroupTag.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                handleTagEvent(
                    viewModel.addTagItem(
                        etGroupTag.text.toString()
                    )
                )
                binding.etGroupTag.text.clear()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        btComplete.setOnClickListener {
            showProgressOverlay()
            Log.d("1111", "complete button")
            viewModel.checkValid(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
            )
        }

        ivPostImage.setOnClickListener {
            openGallery(galleryResultLauncher)
        }

        ivFinish.setOnClickListener {
            activity?.finish()
        }
    }

    private fun setEntityTypeView() = with(binding) {
        tvLimitedPeople.text = getString(R.string.limited_people, LIMITED_PEOPLE)
        includeStepperPersonnel.ivPlusPersonnel.setOnClickListener {
            viewModel.incrementCount()

        }
        includeStepperPersonnel.ivMinusPersonnel.setOnClickListener {
            viewModel.decrementCount()
        }
        ivAddRecruit.setOnClickListener {
            viewModel.onClickRecruit()
        }
    }

    private fun setListAdapter() = with(binding) {
        recyclerViewTags.adapter = tagListAdapter
        recyclerViewRecruit.adapter = recruitListAdapter
    }

    private fun handleTagEvent(result: TagEvent) {
        val messageResId = when (result) {
            TagEvent.Success -> return
            TagEvent.MaxNumberExceeded -> getString(
                R.string.message_max_number, Constants.LIMITED_TAG_COUNT
            )

            TagEvent.TagAlreadyExists -> getString(R.string.message_already_exists)
        }
        requireContext().showToast(messageResId)
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                etTitle.setText(state.title)
                etDescription.setText(state.description)
                tvProjectDescriptionInfo.text = state.descriptionMessage
                etTitlePrecautions.setText(state.precautions)
                etRecruitInfo.setText(state.recruitInfo)
                ivPostImage.load(state.selectedImageUrl ?: state.imageUrl)
                ivPostImageBg.load(state.selectedImageUrl ?: state.imageUrl)

                if (state.groupType == GroupType.PROJECT) {
                    binding.tvTitle.text = getString(R.string.title_project_name)
                    binding.tvProjectDescription.text =
                        getString(R.string.title_project_description)
                    binding.tvContentType.text = getString(R.string.bt_project)
                } else {
                    binding.tvTitle.text = getString(R.string.title_study_name)
                    binding.tvProjectDescription.text = getString(R.string.title_study_description)
                    binding.tvContentType.text = getString(R.string.bt_study)
                }

                recruitListAdapter.submitList(state.recruit)
                tagListAdapter.submitList(state.tags?.map { TagListItem.Item(name = it) })

                if (state.selectedImageUrl != null) {
                    binding.ivPostImageBg.applyDarkFilter()
                }

                val maxPersonnel = state.getTotalMaxPersonnel()
                val mainColor = ContextCompat.getColor(requireContext(), R.color.main_color)
                val coloredText = highlightNumbers(
                    getString(R.string.total_personnel, maxPersonnel),
                    mainColor
                )
                tvTotalRecruit.setText(coloredText, TextView.BufferType.SPANNABLE)
            }
        }


        lifecycleScope.launch {
            complete.collect { newKey ->
//                sharedViewModel.setKey(newKey)
//                parentFragmentManager.beginTransaction().apply {
//                    replace(R.id.fg_activity_post, RecommendFragment())
//                    commit()
//                }
            }
        }

        errorUiState.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            if (it.message == PostErrorMessage.PASS) {
                Log.d("1111", "message= ${it.message}")
                viewModel.createPost(
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString(),
                    binding.etTitlePrecautions.text.toString(),
                    binding.etRecruitInfo.text.toString(),
                    onSuccess = {
                        hideProgressOverlay()
                    },
                    onError = { exception ->
                        hideProgressOverlay()
                        requireContext().showToast(
                            exception.message ?: getString(R.string.post_register_fail)
                        )
                    })
            }
        }

        event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is PostEvent.DialogEvent -> {
                    val maxPersonnel = event.recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0

                    if (maxPersonnel < LIMITED_PEOPLE) {
                        val recruitTypes = event.recruit?.map { it.type } ?: emptyList()

                        RecruitDialog(
                            maxPersonnel,
                            recruitTypes,
                            onConfirmed = { entity ->
                                viewModel.addRecruitInfo(entity)
                            }
                        ) {}.show(requireActivity().supportFragmentManager, null)
                    } else {
                        requireContext().showToast(
                            getString(
                                R.string.limited_people,
                                LIMITED_PEOPLE
                            )
                        )
                    }

                }
            }
        }

//        selectedCount.observe(viewLifecycleOwner) {
//            binding.includeStepperPersonnel.tvRecruitPersonnel.text = it.toString()
//        }
    }

    private fun viewByGroupType(groupType: GroupType) {
        with(binding) {
            constraintLayoutProject.isVisible = groupType == GroupType.PROJECT
            constraintLayoutStudy.isVisible = groupType != GroupType.PROJECT
        }
    }

}