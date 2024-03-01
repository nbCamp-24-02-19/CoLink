package com.seven.colink.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.seven.colink.R
import com.seven.colink.databinding.ActivityChatRoomBinding
import com.seven.colink.ui.chat.adapter.ChatRoomAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity() {

    private val adapter by lazy {
        ChatRoomAdapter()
    }

    private val binding by lazy {
        ActivityChatRoomBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        setAdapter()
    }

    private fun setAdapter() {
        TODO("Not yet implemented")
    }
}