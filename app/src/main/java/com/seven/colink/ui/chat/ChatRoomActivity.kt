package com.seven.colink.ui.chat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ActivityChatRoomBinding
import com.seven.colink.ui.chat.adapter.ChatRoomAdapter
import com.seven.colink.ui.chat.viewmodel.ChatRoomViewModel
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity() {

    companion object {
        const val CHAT_ID = "chatId"
        const val CHAT_TITLE = "title"

        fun newIntent(
            context: Context,
            roomId: String,
            title: String,
            ) = Intent(context, ChatRoomActivity()::class.java).apply {
            putExtra(CHAT_ID, roomId)
            putExtra(CHAT_TITLE, title)
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
                observeMessage(it)
                binding.tvChatRoomTitle.text = intent.getStringExtra(CHAT_TITLE)
            }
        }

        lifecycleScope.launch {
            messageList.collect{
                adapter.submitList(it)
                hideProgressOverlay()
            }
        }
    }

    private fun initView() {
        setAdapter()
        setButton()
        setTextChangeListener()
        showProgressOverlay()
    }

    private fun setButton() = with(binding) {
        btChatroomSend.isEnabled = false
        ivChatRoomBack.setOnClickListener {
            finish()
        }
        btChatroomSend.setOnClickListener {
            viewModel.sendMessage(etChatroomMessage.text.toString())
            etChatroomMessage.text?.clear()
        }
    }

    private fun setTextChangeListener() = with(binding){
        etChatroomMessage.addTextChangedListener {
            btChatroomSend.isEnabled = it?.isNotEmpty() ?: false
        }
    }

    private fun setAdapter() = with(binding) {
        rcChatRoom.adapter = adapter

        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    val lastVisiblePosition = (rcChatRoom.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if (lastVisiblePosition == -1 || positionStart >= adapter.itemCount - 1 && lastVisiblePosition == positionStart - 1) {
                        rcChatRoom.scrollToPosition(positionStart)
                    }
                }
            }
        )
    }
}