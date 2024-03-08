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
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.ui.group.board.list.ApplyRequestFragment
import com.seven.colink.ui.post.content.adapter.PostContentListAdapter
import com.seven.colink.ui.post.content.model.ContentOwnerButtonUiState
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.ui.post.content.viewmodel.PostContentViewModel
import com.seven.colink.ui.post.register.post.PostFragment
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.dialog.setUserInfoDialog
import com.seven.colink.util.status.ApplicationStatus
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
            onClickItem = { _, item -> handleItemClick(item) },
            onClickButton = { _, item, buttonUiState -> handleButtonClick(item, buttonUiState) },
            onClickView = { item, view ->
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
        uiState.observe(requireActivity()) { items ->
            postContentListAdapter.submitList(items)
        }

        updateButtonUiState.observe(requireActivity()) {
            binding.tvEdit.visibility =
                if (it == ContentOwnerButtonUiState.Owner) View.VISIBLE else View.GONE
        }

        dialogUiState.observe(requireActivity()) { state ->
            showDialog(state)
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

    private fun handleItemClick(item: PostContentItem) {
        when (item) {
            is PostContentItem.MemberItem -> {}
            else -> throw UnsupportedOperationException("Unhandled type: $item")
        }
    }

    private fun handleButtonClick(item: PostContentItem, buttonUiState: ContentOwnerButtonUiState) {
        when (item) {
            is PostContentItem.RecruitItem -> handleRecruitItemClick(item, buttonUiState)
            else -> throw UnsupportedOperationException("Unhandled type: $item")
        }
    }

    private fun handleRecruitItemClick(
        item: PostContentItem.RecruitItem,
        buttonUiState: ContentOwnerButtonUiState
    ) {
        when (buttonUiState) {
            ContentOwnerButtonUiState.User -> {
                viewModel.createDialog(item)
            }

            ContentOwnerButtonUiState.Owner -> {
                lifecycleScope.launch {
                    val userEntities = viewModel.getUserEntitiesFromRecruit(item)
                    if (userEntities.isNotEmpty()) {
                        showUserEntitiesDialog(userEntities, item)
                    }
                }
            }
        }
    }

    private fun showDialog(state: DialogUiState?) {
        state?.recruitItem?.let {
            requireContext().setDialog(
                title = getString(R.string.support_dialog_title, state.title),
                message = getString(R.string.support_dialog_message, state.message, state.title),
                R.drawable.img_dialog_study,
                confirmAction = {
                    lifecycleScope.launch {
                        val result = viewModel.applyForProject(
                            recruitItem = state.recruitItem
                        )
                        // TODO 결과 메세지
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
        userEntities.setUserInfoDialog(requireContext()) { userEntity, isRefuseButton ->
            lifecycleScope.launch {
                viewModel.addMemberStatusApprove(
                    if (isRefuseButton) ApplicationStatus.APPROVE else ApplicationStatus.REJECTED,
                    userEntity,
                    item
                )
            }
        }.show()
    }
}