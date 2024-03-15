package com.seven.colink.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.FragmentChatTabBinding
import com.seven.colink.ui.chat.adapter.ChatListAdapter
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.ui.chat.viewmodel.ChatTabViewModel
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatTabFragment : Fragment() {

    companion object {
        const val CHAT_TYPE = "chatType"

        fun newInstance(chatType: ChatTabType) = ChatTabFragment().apply {
            arguments = Bundle().apply {
                putSerializable(CHAT_TYPE, chatType)
            }
        }
    }

    private var _binding: FragmentChatTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatTabViewModel by viewModels()

    private val adapter by lazy {
        ChatListAdapter(
            onClick = {
                startActivity(ChatRoomActivity.newIntent(requireContext(), it.key, it.title))
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel) {
        lifecycleScope.launch {
            chatType.collect {
                setChat()
            }
        }

        lifecycleScope.launch {
            chatList.collect { state ->
                when (state) {
                    is UiState.Loading -> showProgressOverlay()
                    is UiState.Success -> {
                        binding.clEmptyChat.isVisible = state.data.isNullOrEmpty()
                        binding.rcChatList.isVisible = state.data.isNullOrEmpty().not()
                        adapter.submitList(state.data)
                        hideProgressOverlay()
                    }

                    is UiState.Error -> Toast.makeText(
                        context,
                        "${state.throwable}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun initView() {
        setList()
    }

    private fun setList() = with(binding) {
        rcChatList.adapter = adapter
        rcChatList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        _binding = null
        hideProgressOverlay()
        super.onDestroy()
    }
}