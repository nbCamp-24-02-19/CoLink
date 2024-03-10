package com.seven.colink.ui.post.register.post

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentPostBinding
import com.seven.colink.ui.post.register.post.adapter.PostListAdapter
import com.seven.colink.ui.post.register.post.model.PostListItem
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.ui.post.register.post.viewmodel.PostViewModel
import com.seven.colink.ui.post.register.recommend.RecommendFragment
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.util.dialog.RecruitDialog
import com.seven.colink.util.openGallery
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding: FragmentPostBinding get() = _binding!!

    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private val viewModel: PostViewModel by viewModels()
    private val sharedViewModel: PostSharedViewModel by activityViewModels()

    private val postListAdapter: PostListAdapter by lazy {
        PostListAdapter(
            requireContext(),
            onChangedFocus = { position, title, description, item ->
                when (item) {
                    is PostListItem.PostItem -> {
                        viewModel.updatePostItemText(position, title, description)
                    }
                    is PostListItem.PostOptionItem -> {
                        viewModel.updatePostOprionItemText(position, title, description)

                    }
                     else -> Unit
                }
            },
            onClickView = { view, item ->
                when (view.id) {
                    R.id.iv_post_image -> {
                        openGallery(galleryResultLauncher)
                    }

                    R.id.iv_add_recruit -> {
                        when (item) {
                            is PostListItem.RecruitItem -> {
                                val maxPersonnel = item.recruit?.sumOf { it.maxPersonnel ?: 0 } ?: 0
                                val recruitTypes = item.recruit?.map { it.type } ?: emptyList()

                                RecruitDialog(
                                    maxPersonnel,
                                    recruitTypes,
                                    onConfirmed = { entity ->
                                        viewModel.addRecruitInfo(entity)
                                    }
                                ) {}.show(requireActivity().supportFragmentManager, null)
                            }

                            else -> {

                            }
                        }
                    }

                    R.id.iv_plus_personnel -> {
                        viewModel.incrementCount()
                    }

                    R.id.iv_minus_personnel -> {
                        viewModel.decrementCount()
                    }

                    R.id.post_complete -> {
                        viewModel.arePostListItemFieldsValid()
                    }


                }
            },
            onGroupImageClick = { tag ->
                viewModel.checkValidAddTag(tag)
            },
            tagAdapterOnClickItem = { _, item ->
                when (item) {
                    is TagListItem.Item -> {
                        viewModel.removeTagItem(item.name)
                    }

                    else -> Unit
                }
            },
            recruitAdapterOnClickItem = { _, item ->
                viewModel.removeRecruitInfo(item.type)
            }

        )
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
                viewModel.setImageResult(result.data)
            }
        }
    }

    private fun initView() = with(binding) {
        setListAdapter()

        ivFinish.setOnClickListener {
            activity?.finish()
        }
    }

    private fun setListAdapter() = with(binding) {
        recyclerViewPost.adapter = postListAdapter
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) {
            with(binding) {
                postListAdapter.submitList(it)

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

        errorUiState.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            Log.d("77777", "${it.message}")
            if (it.message == PostErrorMessage.PASS) {

//                viewModel.createPost(
//                    onSuccess = {
//                        hideProgressOverlay()
//                    },
//                    onError = { exception ->
//                        hideProgressOverlay()
//                        requireContext().showToast(
//                            exception.message ?: getString(R.string.post_register_fail)
//                        )
//                    })
            }

            if (it.tag != PostErrorMessage.PASS) {
                requireContext().showToast(getString(it.tag.message1, it.tag.message2))
            }
        }
    }
}


