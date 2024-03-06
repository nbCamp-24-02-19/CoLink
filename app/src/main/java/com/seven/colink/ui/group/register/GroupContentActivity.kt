package com.seven.colink.ui.group.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import coil.load
import com.seven.colink.R
import com.seven.colink.databinding.ActivityGroupContentBinding
import com.seven.colink.domain.entity.TagEntity
import com.seven.colink.ui.post.register.post.adapter.TagListAdapter
import com.seven.colink.ui.post.register.post.model.AddTagResult
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.Constants
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.showToast
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupContentActivity : AppCompatActivity() {
    companion object {
        fun newIntentForCreate(
            context: Context
        ) = Intent(context, GroupContentActivity::class.java).apply {
            putExtra(Constants.EXTRA_ENTRY_TYPE, PostEntryType.CREATE)
        }

        fun newIntentForUpdate(
            context: Context,
            entityKey: String
        ) = Intent(context, GroupContentActivity::class.java).apply {
            putExtra(Constants.EXTRA_ENTRY_TYPE, PostEntryType.UPDATE)
            putExtra(Constants.EXTRA_POST_ENTITY, entityKey)
        }
    }

    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private val binding: ActivityGroupContentBinding by lazy {
        ActivityGroupContentBinding.inflate(layoutInflater)
    }

    private val viewModel: GroupContentViewModel by viewModels()

    private val tagListAdapter: TagListAdapter by lazy {
        TagListAdapter(
            onClickItem = { _, item ->
                when (item) {
                    is TagListItem.Item -> {
                        viewModel.removeTagItem(item.tagEntity?.key)
                    }

                    is TagListItem.ContentItem -> Unit
                }
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
        recyclerViewTags.adapter = tagListAdapter

        etGroupTag.setOnEditorActionListener { _, actionId, event ->
            if ((actionId == EditorInfo.IME_ACTION_DONE || (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER))
                && etGroupTag.text.toString().trim().isNotBlank()
            ) {
                handleTagAddResult(viewModel.addTagItem(TagEntity(name = etGroupTag.text.toString().trim())))
                etGroupTag.text.clear()
                etGroupTag.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        tvComplete.setOnClickListener {
            showProgressOverlay()
            viewModel.handleGroupEntity(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString()
            )
        }

        ivGroupImage.setOnClickListener {

        }
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(this@GroupContentActivity) { state ->
            binding.etTitle.setText(state?.title)
            binding.etDescription.setText(state?.description)
            tagListAdapter.submitList(state?.tags?.map { TagListItem.Item(tagEntity = it) })
            binding.ivGroupImage.load(state?.imageUrl)

            when (state?.groupTypeUiState) {
                GroupTypeUiState.Project -> {
                    binding.tvGroupType.backgroundTintList =
                        ContextCompat.getColorStateList(
                            this@GroupContentActivity,
                            R.color.forth_color
                        )
                    binding.tvGroupType.text = getString(R.string.project_kor)
                }

                GroupTypeUiState.Study -> {
                    binding.tvGroupType.backgroundTintList =
                        ContextCompat.getColorStateList(
                            this@GroupContentActivity,
                            R.color.study_color
                        )
                    binding.tvGroupType.text = getString(R.string.study_kor)
                }

                else -> Unit
            }

            when (state?.buttonUiState) {
                ContentButtonUiState.Create -> {
                    binding.tvComplete.text = "등록"
                }

                ContentButtonUiState.Update -> {
                    binding.tvComplete.text = "완료"
                }

                else -> Unit
            }
        }

        groupOperationResult.observe(this@GroupContentActivity) {
            hideProgressOverlay()
            finish()
        }
    }

    private fun handleTagAddResult(result: AddTagResult) {
        val messageResId = when (result) {
            AddTagResult.Success -> return
            AddTagResult.MaxNumberExceeded -> getString(
                R.string.message_max_number,
                Constants.LIMITED_TAG_COUNT
            )

            AddTagResult.TagAlreadyExists -> getString(R.string.message_already_exists)
        }
        showToast(messageResId)
    }
}