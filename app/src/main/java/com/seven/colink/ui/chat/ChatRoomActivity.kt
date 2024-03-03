package com.seven.colink.ui.chat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.seven.colink.databinding.ActivityChatRoomBinding
import com.seven.colink.ui.chat.adapter.ChatRoomAdapter
import com.seven.colink.ui.chat.viewmodel.ChatRoomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity() {

    companion object {
        const val CHAT_ID = "chatId"

        fun newIntent(
            context: Context,
            roomId: String,
            ) = Intent(context, ChatRoomActivity()::class.java).apply {
            putExtra(CHAT_ID, roomId)
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
        lifecycleScope.launch {
            chatRoom.collect{
                setMessages(it)
                observeMessage(it)
            }
        }

        lifecycleScope.launch {
            messageList.collect{
                adapter.submitList(it)
            }
        }
    }

    private fun initView() {
        setAdapter()
        setButton()
    }

    private fun setButton() = with(binding) {
        ivChatRoomBack.setOnClickListener {
            finish()
        }
        btChatroomSend.setOnClickListener {
            viewModel.sendMessage(etChatroomMessage.text.toString())
        }
    }

    private fun setAdapter() = with(binding) {
        rcChatRoom.adapter = adapter
    }
}