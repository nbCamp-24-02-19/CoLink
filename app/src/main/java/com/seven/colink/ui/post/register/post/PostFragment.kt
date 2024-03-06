package com.seven.colink.ui.post.register.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.seven.colink.domain.entity.TagEntity
import com.seven.colink.ui.post.register.post.adapter.RecruitListAdapter
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.AddTagResult
import com.seven.colink.ui.post.register.post.model.DialogEvent
import com.seven.colink.ui.post.register.post.model.PostViewState
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.ui.post.register.post.viewmodel.PostViewModel
import com.seven.colink.ui.post.register.recommend.RecommendFragment
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.dialog.RecruitDialog
import com.seven.colink.util.openGallery
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.showToast
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : Fragment() {
    private val binding: FragmentPostBinding by lazy {
        FragmentPostBinding.inflate(layoutInflater)
    }
    private var totalPersonnelCount = 0
    private var selectedImageUri: Uri? = null
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
    private var recruitTypes: List<String> = emptyList()

    private val viewModel: PostViewModel by viewModels()
    private val sharedViewModel: PostSharedViewModel by activityViewModels()

    private val tagListAdapter: TagListAdapter by lazy {
        TagListAdapter(onClickItem = { _, item ->
            when (item) {
                is TagListItem.Item -> viewModel.removeTagItem(item.tagEntity?.key)
                is TagListItem.ContentItem -> Unit
            }
        })
    }

    private val recruitListAdapter: RecruitListAdapter by lazy {
        RecruitListAdapter(onClickItem = { _, entity ->
            viewModel.removeRecruitInfo(entity.type)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                with(viewModel){
                    if (it != null) {
                        setGroupType(it)
                    }
                }
            }
        }

        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.setEntity(it)
                }
            }
        }

        lifecycleScope.launch {
            entryType.collect {
                with(viewModel) {
                    if (it != null) {
                        setEntryType(it)
                        viewModel.initViewStateByEntryType()
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
        initProjectView()
        initStudyView()
        tvLimitedPeople.text = getString(R.string.limited_people, LIMITED_PEOPLE)
        recyclerViewTags.adapter = tagListAdapter
        recyclerViewRecruit.adapter = recruitListAdapter

        etRegisterTag.setOnEditorActionListener { _, actionId, event ->
            if ((actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) && etRegisterTag.text.toString()
                    .trim().isNotBlank()
            ) {
                handleTagAddResult(
                    viewModel.addTagItem(
                        TagEntity(
                            name = etRegisterTag.text.toString().trim()
                        )
                    )
                )
                etRegisterTag.text.clear()
                etRegisterTag.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }


        ivAddImage.setOnClickListener {
            openGallery(galleryResultLauncher)
        }

        btComplete.setOnClickListener {
            showProgressOverlay()
            viewModel.registerPost(binding.etTitle.text.toString(),
                binding.etContent.text.toString(),
                onSuccess = {
                    hideProgressOverlay()
                },
                onError = { exception ->
                    hideProgressOverlay()
                    requireContext().showToast(exception.message ?: getString(R.string.post_register_fail))
                })
        }

        ivFinish.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initProjectView() {
        binding.ivAddRecruit.setOnClickListener {
            if (totalPersonnelCount < LIMITED_PEOPLE) {
                showRecruitDialog()
            } else {
                requireContext().showToast(getString(R.string.limited_people, LIMITED_PEOPLE))
            }
        }
    }

    private fun initStudyView() = with(binding) {
        includeStepperPersonnel.ivPlusPersonnel.setOnClickListener {
            viewModel.incrementCount()
        }

        includeStepperPersonnel.ivMinusPersonnel.setOnClickListener {
            viewModel.decrementCount()
        }
    }

    private fun showRecruitDialog() {
        RecruitDialog(totalPersonnelCount, recruitTypes, onConfirmed = { entity ->
            viewModel.addRecruitInfo(entity)
        }, onCancelled = {}).show(requireActivity().supportFragmentManager, null)
    }

    private fun handleTagAddResult(result: AddTagResult) {
        val messageResId = when (result) {
            AddTagResult.Success -> return
            AddTagResult.MaxNumberExceeded -> getString(
                R.string.message_max_number, Constants.LIMITED_TAG_COUNT
            )

            AddTagResult.TagAlreadyExists -> getString(R.string.message_already_exists)
        }
        requireContext().showToast(messageResId)
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) { state ->
            with(binding) {
                if (state.isUpdated == true) {
                    etContent.setText(state.editTextContent)
                } else {
                    etContent.hint = state.editTextContent
                }

                etTitle.setText(state.editTextTitle)

                state.isProjectSelected?.let {
                    setTextViewProperties(
                        tvProject, it, state.projectButtonTextColor
                    )
                }
                state.isProjectSelected?.let {
                    setTextViewProperties(
                        tvStudy, it.not(), state.studyButtonTextColor
                    )
                }
            }
        }

        postUiState.observe(viewLifecycleOwner) { state ->
            recruitListAdapter.submitList(state.recruitList)
            binding.tvTotalRecruit.text =
                getString(R.string.total_personnel, state.totalPersonnelCount)
            totalPersonnelCount = state.totalPersonnelCount ?: 0
            state.recruitList?.let {
                recruitTypes = it.map { recruitInfo -> recruitInfo.type.orEmpty() }
            }

            tagListAdapter.submitList(state.tagList?.map {
                com.seven.colink.ui.post.register.post.model.TagListItem.Item(
                    tagEntity = it
                )
            })
        }

        selectedPersonnelCount.observe(viewLifecycleOwner) { count ->
            binding.includeStepperPersonnel.tvRecruitPersonnel.text = count.toString()
        }

        selectedImage.observe(viewLifecycleOwner) { selected ->
            val imageUrl = selected?.newImage ?: selected?.originImage
            if (imageUrl != null) {
                binding.ivAddImage.load(imageUrl)
                binding.ivImageBackground.load(imageUrl)
                selectedImageUri = imageUrl
            }
        }

        lifecycleScope.launch {
            complete.collect { newKey ->
                sharedViewModel.setKey(newKey)
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fg_activity_post, RecommendFragment())
                    commit()
                }
            }
        }

    }

    private fun setTextViewProperties(textView: TextView, isSelected: Boolean, textColorRes: Int?) {
        textView.isSelected = isSelected
        textColorRes?.let { textView.setTextColor(ContextCompat.getColor(requireContext(), it)) }
        binding.constraintLayoutProject.isVisible = isSelected.not()
        binding.constraintLayoutStudy.isVisible = isSelected
    }
}