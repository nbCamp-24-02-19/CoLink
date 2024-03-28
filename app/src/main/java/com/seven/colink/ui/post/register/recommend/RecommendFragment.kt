package com.seven.colink.ui.post.register.recommend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.R
import com.seven.colink.databinding.UtilRecommendDialogBinding
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.group.GroupActivity
import com.seven.colink.ui.post.register.recommend.adapter.RecommendAdapter
import com.seven.colink.ui.post.register.recommend.viewmodel.RecommendViewModel
import com.seven.colink.ui.post.register.setgroup.SetGroupFragment
import com.seven.colink.ui.post.register.viewmodel.PostSharedViewModel
import com.seven.colink.ui.userdetail.UserDetailActivity
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import com.seven.colink.util.status.SnackType
import com.seven.colink.util.status.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendFragment : Fragment() {

    private val binding by lazy {
        UtilRecommendDialogBinding.inflate(layoutInflater)
    }
    private val viewModel: RecommendViewModel by viewModels()
    private val sharedViewModel: PostSharedViewModel by activityViewModels()
    private val adapter by lazy {
        RecommendAdapter(
            inviteGroup = {
                viewModel.invitePost(it)
                binding.root.setSnackBar(SnackType.Success, "알림을 전송했습니다.")
            },
            editGroup = {
                startActivity(
                    GroupActivity.newIntent(
                        context = requireContext(),
                        entryType = PostEntryType.UPDATE,
                        key = it,
                    )
                )
            },
            onChat = {
                viewModel.setChatRoom(it)
            },
            onNext = {
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.fg_activity_post, SetGroupFragment())
                    commit()
                }
            },
            onDetail = {
                startActivity(UserDetailActivity.newIntent(requireActivity(), it))
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
    }

    private fun initSharedViewModel() = with(sharedViewModel) {
        lifecycleScope.launch {
            key.collect {
                if (it != null) {
                    viewModel.loadList(key = it)
                }
            }
        }
        lifecycleScope.launch {
            groupType.collect {
                if (it != GroupType.PROJECT) {
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.fg_activity_post, SetGroupFragment())
                        commit()
                    }
                }
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            chatRoomEvent.collect {
                startActivity(
                    ChatRoomActivity.newIntent(requireContext(), it.key)
                )
            }
        }

        lifecycleScope.launch {
            recommendList.collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> showProgressOverlay()
                    is UiState.Success -> {
                        hideProgressOverlay()
                        adapter.submitList(uiState.data)
                    }

                    is UiState.Error -> {
                        /*TODO 예외 처리 다이얼로그*/
                        hideProgressOverlay()
                        Toast.makeText(
                            requireContext(),
                            "${uiState.throwable.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("RecommendFragment", "${uiState.throwable.message}")
                        requireActivity().finish()
                    }
                }
            }
        }

        lifecycleScope.launch {
            inviteEvent.collect {
                binding.root.setSnackBar(SnackType.Success, it).show()
            }
        }
    }

    private fun initView() = with(binding) {
        rcRecommendList.adapter = adapter
    }

}