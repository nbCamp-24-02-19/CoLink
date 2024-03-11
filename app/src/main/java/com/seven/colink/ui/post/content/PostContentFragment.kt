package com.seven.colink.ui.post.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentPostContentBinding
import com.seven.colink.ui.group.board.list.ApplyRequestFragment
import com.seven.colink.ui.post.content.adapter.PostContentListAdapter
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.ui.post.content.viewmodel.PostContentViewModel
import com.seven.colink.ui.post.register.post.model.PostErrorMessage
import com.seven.colink.ui.post.register.post.PostFragment
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.showToast
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PostContentFragment : Fragment() {
    private var _binding: FragmentPostContentBinding? = null
    private val binding: FragmentPostContentBinding get() = _binding!!

    private val viewModel: PostContentViewModel by viewModels()
    private val sharedViewModel: PostSharedViewModel by activityViewModels()

    private val postContentListAdapter by lazy {
        PostContentListAdapter(
            requireContext(),
            onClickItem = { item -> },
            onClickButton = { item, buttonUiState -> handleButtonClick(item, buttonUiState) },
            onClickView = { view ->
                when (view.id) {
                    R.id.tv_apply_request -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(
                                R.id.fg_activity_post,
                                ApplyRequestFragment()
                            )
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewPostContent.adapter = postContentListAdapter

        binding.ivFinish.setOnClickListener {
            activity?.finish()
        }

        binding.tvEdit.setOnClickListener {
            lifecycleScope.launch {
                sharedViewModel.setEntryType(PostEntryType.UPDATE)
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fg_activity_post, PostFragment())
                    commit()
                }
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        uiState.observe(viewLifecycleOwner) { items ->
            postContentListAdapter.submitList(items)
        }

        updateButtonUiState.observe(viewLifecycleOwner) {
            binding.tvEdit.visibility =
                if (it == ContentButtonUiState.Manager) View.VISIBLE else View.GONE
        }

        dialogUiState.observe(viewLifecycleOwner) { state ->
            if (state == null) {
                return@observe
            }

            showDialog(state)
        }

        errorUiState.observe(requireActivity()) { errorState ->
            errorState ?: return@observe

            val messageResId = when (errorState.message) {
                PostErrorMessage.ALREADY_SUPPORT, PostErrorMessage.SUCCESS_SUPPORT -> {
                    errorState.message.message1
                }

                else -> return@observe
            }

            requireContext().showToast(getString(messageResId))
        }
    }

    private fun initSharedViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.setEntity(it)
                    viewModel.initViewStateByEntity()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun handleButtonClick(item: PostContentItem, buttonUiState: ContentButtonUiState) {
        when (item) {
            is PostContentItem.RecruitItem -> handleRecruitItemClick(item, buttonUiState)
            else -> throw UnsupportedOperationException("Unhandled type: $item")
        }
    }

    private fun handleRecruitItemClick(
        item: PostContentItem.RecruitItem,
        buttonUiState: ContentButtonUiState
    ) {
        when (buttonUiState) {
            ContentButtonUiState.User -> {
                viewModel.createDialog(item)
            }
            else -> Unit
        }
    }

    private fun showDialog(state: DialogUiState?) {
        state?.recruitItem?.let {
            requireContext().setDialog(
                title = getString(R.string.support_dialog_title, state.title),
                message = getString(R.string.support_dialog_message, state.message, state.title),
                image = if (state.groupType == GroupType.PROJECT) R.drawable.img_dialog_project else R.drawable.img_dialog_study,
                confirmAction = {
                    it.dismiss()
                    lifecycleScope.launch {
                        viewModel.applyForProject(
                            recruitItem = state.recruitItem
                        )
                    }
                    it.dismiss()
                },
                cancelAction = { it.dismiss() }
            ).show()
        }
    }

}