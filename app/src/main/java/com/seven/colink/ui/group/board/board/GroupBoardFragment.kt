package com.seven.colink.ui.group.board.board


import com.seven.colink.ui.group.calendar.material.MaterialCalendarFragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.FragmentGroupBoardBinding
import com.seven.colink.ui.group.board.board.adapter.GroupBoardListAdapter
import com.seven.colink.ui.group.board.list.ApplyRequestFragment
import com.seven.colink.ui.group.content.GroupContentFragment
import com.seven.colink.ui.group.viewmodel.GroupSharedViewModel
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.ui.promotion.ProductPromotionActivity
import com.seven.colink.ui.userdetail.UserDetailActivity
import com.seven.colink.util.Constants
import com.seven.colink.util.dialog.setDialog
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import com.seven.colink.util.status.ProjectStatus
import com.seven.colink.util.status.SnackType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GroupBoardFragment : Fragment() {
    private var _binding: FragmentGroupBoardBinding? = null
    private val binding: FragmentGroupBoardBinding get() = _binding!!
    private val viewModel: GroupBoardViewModel by viewModels()
    private val sharedViewModel: GroupSharedViewModel by activityViewModels()

    private val groupBoardListAdapter by lazy {
        GroupBoardListAdapter(
            onClickItem = { item ->
                when (item) {
                    is GroupBoardItem.PostItem -> {
                        startActivity(
                            PostActivity.newIntent(
                                context = requireActivity(),
                                key = item.post.key
                            )
                        )

                    }

                    is GroupBoardItem.MemberItem -> {
                        startActivity(
                            UserDetailActivity.newIntent(
                                requireActivity(),
                                item.userInfo.uid ?: return@GroupBoardListAdapter
                            )
                        )
                    }

                    else -> Unit
                }
            },
            onClickView = { _, view ->
                when (view.id) {
                    R.id.tv_apply_request -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(
                                R.id.fg_activity_group,
                                ApplyRequestFragment()
                            )
                            addToBackStack(null)
                            commit()
                        }
                    }

                    R.id.iv_calendar, R.id.tv_calendar -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(
                                R.id.fg_activity_group,
                                MaterialCalendarFragment()
                            )
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            },
            onChangeStatus = { item, status ->
                when (status) {
                    ProjectStatus.END -> {
                        when (item) {
                            is GroupBoardItem.GroupItem -> {
                                if (item.groupType == GroupType.STUDY) {
                                    view?.setSnackBar(
                                        SnackType.Error,
                                        requireContext().getString(R.string.promotion_error_message)
                                    )?.show()
                                    return@GroupBoardListAdapter
                                }

                                val intent =
                                    Intent(requireContext(), ProductPromotionActivity::class.java)
                                intent.putExtra(Constants.EXTRA_ENTITY_KEY, item.key)
                                startActivity(intent)
                            }

                            else -> Unit
                        }
                    }

                    else -> {
                        requireContext().setDialog(
                            title = requireContext().getString(R.string.project_status_changed),
                            message = requireContext().getString(
                                R.string.project_status_changed_message,
                                status.getStatusText()
                            ),
                            confirmAction = {
                                viewModel.onChangedStatus(status)
                                it.dismiss()
                            },
                            cancelAction = { it.dismiss() }
                        ).show()

                    }
                }
            }
        )
    }

    private fun ProjectStatus.getStatusText(): String =
        when (this) {
            ProjectStatus.RECRUIT -> requireContext().getString(R.string.status_text_start)
            ProjectStatus.START -> requireContext().getString(R.string.status_text_end)
            else -> ""
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBoardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()

    }

    private fun initView() = with(binding) {
        setListAdapter()
        binding.ivGroupFinish.setOnClickListener {
            activity?.finish()
        }
        binding.tvComplete.setOnClickListener {
            sharedViewModel.setEntryType(PostEntryType.UPDATE)
            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fg_activity_group,
                    GroupContentFragment()
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun setListAdapter() = with(binding) {
        recyclerViewBoard.adapter = groupBoardListAdapter
    }

    private fun initViewModel() = with(viewModel) {
        uiStateList.observe(requireActivity()) {
            groupBoardListAdapter.submitList(it)
        }

        entity.observe(requireActivity()) {
            when (it?.buttonUiState) {
                ContentButtonUiState.Manager -> {
                    binding.tvComplete.visibility = View.VISIBLE
                }

                else -> binding.tvComplete.visibility = View.INVISIBLE
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
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}