package com.seven.colink.ui.post.content

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.ActivityPostContentBinding
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.post.content.adapter.PostContentListAdapter
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentButtonUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.ui.post.content.viewmodel.PostContentViewModel
import com.seven.colink.util.Constants
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.dialog.setUserInfoDialog
import com.seven.colink.util.showToast
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostContentActivity : AppCompatActivity() {
    companion object {
        fun newIntent(
            context: Context,
            entityKey: String
        ) = Intent(context, PostContentActivity::class.java).apply {
            putExtra(Constants.EXTRA_POST_ENTITY, entityKey)
        }
    }

    private val postContentListAdapter by lazy {
        PostContentListAdapter(
            this@PostContentActivity,
            onClickItem = { _, item -> handleItemClick(item) },
            onClickButton = { _, item, buttonUiState -> handleButtonClick(item, buttonUiState) }
        )
    }

    private val binding: ActivityPostContentBinding by lazy {
        ActivityPostContentBinding.inflate(layoutInflater)
    }

    private val viewModel: PostContentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel) {
        postContentItems.observe(this@PostContentActivity) { items ->
            postContentListAdapter.submitList(items)
        }

        updateButtonUiState.observe(this@PostContentActivity) {
            binding.tvEdit.visibility =
                if (it == PostContentButtonUiState.Writer) View.VISIBLE else View.GONE
        }

        dialogUiState.observe(this@PostContentActivity) { state ->
            showDialog(state)
        }

        uiState.observe(this@PostContentActivity) {
            if (it == null) {
                showToast(getString(R.string.failed_error))
                finish()
            }
        }
    }

    private fun initView() = with(binding) {
        incrementPostViews()

        recyclerViewPostContent.adapter = postContentListAdapter

        binding.ivFinish.setOnClickListener {
            finish()
        }

        binding.tvEdit.setOnClickListener {
            lifecycleScope.launch {
                startActivity(
                    PostActivity.newIntentForUpdate(
                        this@PostContentActivity,
                        viewModel.getPost() ?: return@launch
                    )
                )
                if (isFinishing.not()) {
                    finish()
                }
            }
        }
    }

    private fun handleItemClick(item: PostContentItem) {
        when (item) {
            is PostContentItem.MemberItem -> {}
            else -> throw UnsupportedOperationException("Unhandled type: $item")
        }
    }

    private fun handleButtonClick(item: PostContentItem, buttonUiState: PostContentButtonUiState) {
        when (item) {
            is PostContentItem.RecruitItem -> handleRecruitItemClick(item, buttonUiState)
            else -> throw UnsupportedOperationException("Unhandled type: $item")
        }
    }

    private fun handleRecruitItemClick(
        item: PostContentItem.RecruitItem,
        buttonUiState: PostContentButtonUiState
    ) {
        when (buttonUiState) {
            PostContentButtonUiState.Supporter -> {
                viewModel.createDialog(item)
            }

            PostContentButtonUiState.Writer -> {
                lifecycleScope.launch {
                    val userEntities = viewModel.getUserEntitiesFromRecruit(item)
                    if (userEntities.isNotEmpty()) {
                        showUserEntitiesDialog(userEntities, item)
                    } else {
                        showToast(getString(R.string.no_support_list))
                    }
                }
            }
        }
    }

    private fun showDialog(state: DialogUiState?) {
        state?.recruitItem?.let {
            setDialog(
                title = getString(R.string.support_dialog_title, state.title),
                message = getString(R.string.support_dialog_message, state.message, state.title),
                R.drawable.img_dialog_study,
                confirmAction = {
                    lifecycleScope.launch {
                        val result = viewModel.applyForProject(
                            recruitItem = state.recruitItem
                        )
                        showToast(result.message)
                        it.dismiss()
                    }
                },
                cancelAction = { it.dismiss() }
            ).show()
        }
    }

    private fun showUserEntitiesDialog(
        userEntities: List<UserEntity>,
        item: PostContentItem.RecruitItem
    ) {
        userEntities.setUserInfoDialog(this) { userEntity, isRefuseButton ->
            lifecycleScope.launch {
                if (isRefuseButton) {
                    viewModel.updateApplicationStatus(
                        ApplicationStatus.APPROVE,
                        userEntity,
                        item
                    )
                } else {
                    viewModel.updateApplicationStatus(
                        ApplicationStatus.REJECTED,
                        userEntity,
                        item
                    )
                }
            }
        }.show()
    }

    private fun incrementPostViews() {
        lifecycleScope.launch {
            viewModel.incrementPostViews()
        }
    }

}