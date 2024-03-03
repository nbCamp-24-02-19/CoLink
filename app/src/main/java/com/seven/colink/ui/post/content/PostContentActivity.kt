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
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.ui.post.adapter.PostContentListAdapter
import com.seven.colink.util.Constants
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostContentActivity : AppCompatActivity() {
    companion object {
        fun newIntentForUpdate(
            context: Context,
            groupType: GroupType,
            position: Int,
            entity: PostEntity
        ) = Intent(context, PostContentActivity::class.java).apply {
            putExtra(Constants.EXTRA_GROUP_TYPE, groupType)
            putExtra(Constants.EXTRA_POSITION_ENTITY, position)
            putExtra(Constants.EXTRA_POST_ENTITY, entity)
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

            binding.tvEdit.visibility = items.filterIsInstance<PostContentItem.RecruitItem>()
                .firstOrNull()?.let {
                    if (it.buttonUiState == PostContentButtonUiState.Writer) View.VISIBLE else View.GONE
                } ?: View.GONE

        }
    }

    private fun initView() = with(binding) {
        recyclerViewPostContent.adapter = postContentListAdapter

        binding.ivFinish.setOnClickListener {
            finish()
        }

        binding.tvEdit.setOnClickListener {

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
            PostContentButtonUiState.Supporter -> showSupportDialog(item)
            PostContentButtonUiState.Writer -> { /* Handle Create click */
            }
        }
    }

    private fun showSupportDialog(recruitItem: PostContentItem.RecruitItem) {
        setDialog(
            title = getString(R.string.support_dialog_title, ""),
            message = getString(R.string.support_dialog_message, "", ""),
            R.drawable.img_dialog_study,
            confirmAction = {
                lifecycleScope.launch {
                    val result = viewModel.applyForProject(recruitItem = recruitItem)
                    it.dismiss()
                }
            },
            cancelAction = { it.dismiss() }
        ).show()
    }
    
}