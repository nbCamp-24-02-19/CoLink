package com.seven.colink.ui.post.content

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.ActivityPostContentBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.post.adapter.PostContentListAdapter
import com.seven.colink.util.Constants
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.dialog.setUserInfoDialog
import com.seven.colink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostContentActivity : AppCompatActivity() {
    companion object {
        fun newIntentForUpdate(
            context: Context,
//            position: Int,
            entity: PostEntity
        ) = Intent(context, PostContentActivity::class.java).apply {
//            putExtra(Constants.EXTRA_POSITION_ENTITY, position)
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

        dialogUiState.observe(this@PostContentActivity) { state ->
            showDialog(state)
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
            PostContentButtonUiState.Supporter -> {
                viewModel.createDialog(item)
            }

            PostContentButtonUiState.Writer -> {
                lifecycleScope.launch {
                    val userEntities = viewModel.getUserEntitiesFromRecruit()
                    if (userEntities.isNotEmpty()) {
                        showUserEntitiesDialog(userEntities)
                    } else {
                        showToast("지원 목록이 없습니다.")
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

    private fun showUserEntitiesDialog(userEntities: List<UserEntity>) {
        userEntities.setUserInfoDialog(this) { user, isRefuseButton ->
            if (isRefuseButton) {

            } else {

            }
        }.show()
    }


}