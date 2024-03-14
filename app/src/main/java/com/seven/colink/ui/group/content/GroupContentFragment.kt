package com.seven.colink.ui.group.content

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentGroupContentBinding
import com.seven.colink.ui.group.content.adapter.GroupContentListAdapter
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.openGallery
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupContentFragment : Fragment() {
    private var _binding: FragmentGroupContentBinding? = null
    private val binding: FragmentGroupContentBinding get() = _binding!!
    private val viewModel: GroupContentViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    private val groupContentListAdapter by lazy {
        GroupContentListAdapter(
            onClickItem = { view ->
                when (view.id) {
                    R.id.iv_group_image -> {
                        openGallery(galleryResultLauncher)
                    }

                }
            },
            onGroupImageClick = {
                viewModel.checkValidAddTag(it)
            },
            tagAdapterOnClickItem = {tagItem ->
                when (tagItem) {
                    is TagListItem.Item -> {
                        viewModel.removeTagItem(tagItem.name)
                    }

                    else -> Unit
                }
            },
            onChangeStatus = {
                viewModel.onChangedStatus(it)
            },
            onChangedFocus = { position, title, description, item ->
                when (item) {
                    is GroupContentItem.GroupContent, is GroupContentItem.GroupOptionItem -> {
                        viewModel.updateGroupItemText(position, title, description)
                    }
                    else -> Unit
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initRegisterForActivityResult()
        initViewModel()
        initSharedViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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
        ivGroupFinish.setOnClickListener {
            if (!parentFragmentManager.isStateSaved) {
                parentFragmentManager.popBackStack()
            }
        }

        tvComplete.setOnClickListener {
            showProgressOverlay()
            lifecycleScope.launch {
                viewModel.onClickUpdate()
            }
        }
    }

    private fun setListAdapter() = with(binding) {
        recyclerViewGroupContent.adapter = groupContentListAdapter
        recyclerViewGroupContent.itemAnimator = null
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            uiState.collect {
                groupContentListAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            errorUiState.collect { errorState ->
                errorState?.let {
                    if (it.tag != GroupErrorMessage.PASS) {
                        requireContext().showToast(getString(it.tag.message1, it.tag.message2))
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.complete.collect {
                hideProgressOverlay()
                if (!parentFragmentManager.isStateSaved) {
                    parentFragmentManager.popBackStack()
                }
            }
        }

    }

    private fun initSharedViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.setEntity(it)
                }
            }
        }

        lifecycleScope.launch {
            entryType.collect {
                if (it != null) {
                    viewModel.setEntryType(it)
                }
            }
        }
    }

}