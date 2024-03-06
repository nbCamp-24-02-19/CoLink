package com.seven.colink.ui.post.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ActivityPostBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.TagEntity
import com.seven.colink.ui.group.register.GroupContentActivity
import com.seven.colink.ui.post.register.adapter.RecruitListAdapter
import com.seven.colink.ui.post.register.adapter.TagListAdapter
import com.seven.colink.ui.post.register.model.AddTagResult
import com.seven.colink.ui.post.register.model.DialogEvent
import com.seven.colink.ui.post.register.model.TagListItem
import com.seven.colink.ui.post.register.viewmodel.PostViewModel
import com.seven.colink.util.Constants.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_GROUP_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_POST_ENTITY
import com.seven.colink.util.Constants.Companion.LIMITED_PEOPLE
import com.seven.colink.util.Constants.Companion.LIMITED_TAG_COUNT
import com.seven.colink.util.dialog.RecruitDialog
import com.seven.colink.util.dialog.setUpGroupDialog
import com.seven.colink.util.openGallery
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.showToast
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    private var totalPersonnelCount = 0
    private var selectedImageUri: Uri? = null
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>
    private var recruitTypes: List<String> = emptyList()

    companion object {
        fun newIntentForCreate(
            context: Context,
            groupType: GroupType
        ) = Intent(context, PostActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, PostEntryType.CREATE)
            putExtra(EXTRA_GROUP_TYPE, groupType)
        }

        fun newIntentForUpdate(
            context: Context,
            entity: PostEntity
        ) = Intent(context, PostActivity::class.java).apply {
            putExtra(EXTRA_ENTRY_TYPE, PostEntryType.UPDATE)
            putExtra(EXTRA_POST_ENTITY, entity.key)
        }
    }

    private val viewModel: PostViewModel by viewModels()

    private val tagListAdapter: TagListAdapter by lazy {
        TagListAdapter(
            onClickItem = { _, item ->
                when (item) {
                    is TagListItem.Item -> viewModel.removeTagItem(item.tagEntity?.key)
                    is TagListItem.ContentItem -> Unit
                }
            }
        )
    }

    private val recruitListAdapter: RecruitListAdapter by lazy {
        RecruitListAdapter(
            onClickItem = { _, entity ->
                viewModel.removeRecruitInfo(entity.type)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initRegisterForActivityResult()
        initViewModel()
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
            if ((actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
                && etRegisterTag.text.toString().trim().isNotBlank()
            ) {
                handleTagAddResult(viewModel.addTagItem(TagEntity(name = etRegisterTag.text.toString().trim())))
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
            viewModel.registerPost(
                binding.etTitle.text.toString(),
                binding.etContent.text.toString(),
                onSuccess = {
                    hideProgressOverlay()
                    finish()
                },
                onError = { exception ->
                    hideProgressOverlay()
                    showToast(exception.message ?: getString(R.string.post_register_fail))
                }
            )
        }

        ivFinish.setOnClickListener {
            finish()
        }
    }

    private fun initProjectView() {
        binding.ivAddRecruit.setOnClickListener {
            if (totalPersonnelCount < LIMITED_PEOPLE) {
                showRecruitDialog()
            } else {
                showToast(getString(R.string.limited_people, LIMITED_PEOPLE))
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
        RecruitDialog(
            totalPersonnelCount,
            recruitTypes,
            onConfirmed = { entity ->
                viewModel.addRecruitInfo(entity)
            },
            onCancelled = {}
        ).show(supportFragmentManager, null)
    }

    private fun handleTagAddResult(result: AddTagResult) {
        val messageResId = when (result) {
            AddTagResult.Success -> return
            AddTagResult.MaxNumberExceeded -> getString(
                R.string.message_max_number,
                LIMITED_TAG_COUNT
            )

            AddTagResult.TagAlreadyExists -> getString(R.string.message_already_exists)
        }
        showToast(messageResId)
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(this@PostActivity) { state ->
            with(binding) {
                if (state.isUpdated == true) {
                    etContent.setText(state.editTextContent)
                } else {
                    etContent.hint = state.editTextContent
                }

                etTitle.setText(state.editTextTitle)

                state.isProjectSelected?.let {
                    setTextViewProperties(
                        tvProject,
                        it,
                        state.projectButtonTextColor
                    )
                }
                state.isProjectSelected?.let {
                    setTextViewProperties(
                        tvStudy,
                        it.not(),
                        state.studyButtonTextColor
                    )
                }
            }
        }

        postUiState.observe(this@PostActivity) { state ->
            recruitListAdapter.submitList(state.recruitList)
            binding.tvTotalRecruit.text =
                getString(R.string.total_personnel, state.totalPersonnelCount)
            totalPersonnelCount = state.totalPersonnelCount ?: 0
            state.recruitList?.let {
                recruitTypes = it.map { recruitInfo -> recruitInfo.type.orEmpty() }
            }

            tagListAdapter.submitList(state.tagList?.map { TagListItem.Item(tagEntity = it) })
        }

        selectedPersonnelCount.observe(this@PostActivity) { count ->
            binding.includeStepperPersonnel.tvRecruitPersonnel.text = count.toString()
        }

        selectedImage.observe(this@PostActivity) { selected ->
            val imageUrl = selected?.newImage ?: selected?.originImage
            if (imageUrl != null) {
                binding.ivAddImage.load(imageUrl)
                binding.ivImageBackground.load(imageUrl)
                selectedImageUri = imageUrl
            }
        }

        showDialog.observe(this@PostActivity) { event ->
            when (event) {
                is DialogEvent.Show -> handleShowDialog(event)
                DialogEvent.Dismiss -> Unit
            }
        }

    }

    private fun handleShowDialog(showEvent: DialogEvent.Show) {
        val dialog = setUpGroupDialog(
            this@PostActivity,
            showEvent.groupType,
            { alertDialog ->
                startActivity(GroupContentActivity.newIntentForUpdate(this@PostActivity, showEvent.key))
                alertDialog.dismiss()
                finish()
            },
            { alertDialog ->
                alertDialog.dismiss()
                finish()
            }
        )
        dialog.show()
    }

    private fun setTextViewProperties(textView: TextView, isSelected: Boolean, textColorRes: Int?) {
        textView.isSelected = isSelected
        textColorRes?.let { textView.setTextColor(ContextCompat.getColor(this@PostActivity, it)) }
        binding.constraintLayoutProject.isVisible = isSelected.not()
        binding.constraintLayoutStudy.isVisible = isSelected
    }

}