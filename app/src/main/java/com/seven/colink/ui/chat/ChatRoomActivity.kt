package com.seven.colink.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.databinding.ActivityChatRoomBinding
import com.seven.colink.ui.chat.adapter.ChatRoomAdapter
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.ui.chat.viewmodel.ChatRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity() {

    companion object {
        const val CHAT_ID = "chatId"

        fun newInstance(chatId: String) = ChatTabFragment().apply {
            arguments = Bundle().apply {
                putSerializable(CHAT_ID, chatId)
            }
        }
    }

    private val adapter by lazy {
        ChatRoomAdapter()
    }

    private val viewModel: ChatRoomViewModel by viewModels()

    private val binding by lazy {
        ActivityChatRoomBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel){
        observeMessage()

        viewModelScope.launch {
            messageList.collect{
                adapter.submitList(it)
            }
        }
    }

    private fun initView() {
        setAdapter()
    }

    private fun setAdapter() = with(binding) {
        rcChatRoom.adapter = adapter
    }
}