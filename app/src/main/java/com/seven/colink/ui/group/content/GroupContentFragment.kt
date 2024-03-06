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
import com.seven.colink.domain.entity.TagEntity
import com.seven.colink.ui.group.content.adapter.GroupContentListAdapter
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import com.seven.colink.ui.post.register.post.model.AddTagResult
import com.seven.colink.ui.post.register.post.model.TagListItem
import com.seven.colink.util.Constants
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

    private val groupContentListAdapter by lazy {
        GroupContentListAdapter(
            requireContext(),
            binding.recyclerViewGroupContent,
            onClickItem = { item, view ->
                when (view.id) {
                    R.id.iv_group_image -> {
                        openGallery(galleryResultLauncher)
                    }

                }
            },
            onGroupImageClick = {
                handleTagAddResult(
                    viewModel.addTagItem(
                        TagEntity(
                            name = it
                        )
                    )
                )
            },
            tagAdapterOnClickItem = { _, tagItem ->
                when (tagItem) {
                    is TagListItem.Item -> {
                        viewModel.removeTagItem(tagItem.key)}
                    else -> Unit
                }
            }
        )
    }
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

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
                viewModel.handleGalleryResult(result.resultCode, result.data)
            }
        }
    }

    private fun initView() = with(binding) {
        recyclerViewGroupContent.adapter = groupContentListAdapter

        ivGroupFinish.setOnClickListener {
            if (!parentFragmentManager.isStateSaved) {
                parentFragmentManager.popBackStack()
            }
        }

        tvComplete.setOnClickListener {
            showProgressOverlay()
            val (etTitle, etDescription) = groupContentListAdapter.getEtTitleAndDescription(0)
            viewModel.handleGroupEntity(etTitle, etDescription)
        }
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(requireActivity()) {
            groupContentListAdapter.submitList(listOf(it))
        }

        groupOperationResult.observe(requireActivity()) {
            hideProgressOverlay()
            parentFragmentManager.popBackStack()
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

    private fun handleTagAddResult(result: AddTagResult) {
        val messageResId = when (result) {
            AddTagResult.Success -> return
            AddTagResult.MaxNumberExceeded -> getString(
                R.string.message_max_number,
                Constants.LIMITED_TAG_COUNT
            )

            AddTagResult.TagAlreadyExists -> getString(R.string.message_already_exists)
        }
        requireActivity().showToast(messageResId)
    }
}