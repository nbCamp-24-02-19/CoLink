package com.seven.colink.ui.post.content

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.seven.colink.databinding.ActivityPostContentBinding
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.ui.post.adapter.PostContentListAdapter
import com.seven.colink.util.Constants
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.AndroidEntryPoint

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
            onClickItem = { position, item ->

            }
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
        postContentItems.observe(this@PostContentActivity) {
            postContentListAdapter.submitList(it)
        }
    }

    private fun initView() = with(binding) {
        recyclerViewPostContent.adapter = postContentListAdapter
    }
}