package com.seven.colink.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.FragmentChatTabBinding
import com.seven.colink.ui.chat.adapter.ChatListAdapter
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.ui.chat.viewmodel.ChatTabViewModel
import com.seven.colink.ui.sign.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatTabFragment: Fragment() {

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
            onClick = { Unit }
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
    }

    private fun initView() {
        setList()
    }

    private fun setList() = with(binding){
        rcChatList.adapter = adapter
        rcChatList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}