package com.seven.colink.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seven.colink.databinding.ActivityChatRoomBinding
import com.seven.colink.ui.chat.adapter.ChatRoomAdapter
import com.seven.colink.ui.chat.viewmodel.ChatRoomViewModel
import com.seven.colink.util.openGallery
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.status.UiState
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
        ChatRoomAdapter(
            onClickLink = {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
            }
        )
    }

    private val viewModel: ChatRoomViewModel by viewModels()

    private val binding by lazy {
        ActivityChatRoomBinding.inflate(layoutInflater)
    }

    private val galleryResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.selectImg(result.data?.data?: return@registerForActivityResult)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(viewModel){
        lifecycleScope.launch {
            chatRoom.collect{ chatRoomEntity ->
                observeMessage(chatRoomEntity)
                binding.tvChatRoomTitle.text = chatRoomEntity.title
            }
        }

        lifecycleScope.launch {
            messageList.collect{ state ->
                when(state) {
                    is UiState.Loading -> {
                        showProgressOverlay()
                    }
                    is UiState.Success -> {
                        adapter.submitList(state.data)
                        hideProgressOverlay()
                        binding.rcChatRoom.scrollToPosition(adapter.itemCount - 1)
                    }
                    is UiState.Error -> Toast.makeText(this@ChatRoomActivity, "${state.throwable}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            selectedImage.collect { uri ->
                with(binding) {
                    if (uri != Uri.EMPTY) {
                        llPreview.isVisible = true
                        ivPreviewImg.load(uri)
                        btChatroomSend.isEnabled = true
                        etChatroomMessage.text?.clear()
                        etChatroomMessage.isEnabled = false
                    } else {
                        llPreview.isVisible = false
                        btChatroomSend.isEnabled = false
                        etChatroomMessage.isEnabled = true
                    }
                }
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
            viewModel.sendMessage(text = etChatroomMessage.text.toString())
            etChatroomMessage.text?.clear()
            binding.llPreview.isVisible = false
            viewModel.emptyImg()
        }

        btChatroomPlus.setOnClickListener {
            openGallery(galleryResultLauncher)
        }

        ivPreviewClose.setOnClickListener {
            viewModel.emptyImg()
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