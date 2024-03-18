package com.seven.colink.ui.notify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.seven.colink.R
import com.seven.colink.databinding.ActivityNotificationBinding
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.notify.adapter.NotificationAdapter
import com.seven.colink.ui.notify.viewmodel.NotificationViewModel
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.SnackType
import com.seven.colink.util.status.UiState
import com.seven.colink.util.status.UiState.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityNotificationBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        NotificationAdapter(
            onChat = {
                startActivity(
                    ChatRoomActivity.newIntent(this,it)
                )
            }
        )
    }

    private val viewmodel: NotificationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() {
        setButton()
        setList()
    }

    private fun setList() = with(binding) {
        rcNotifyList.adapter = adapter
        rcNotifyList.layoutManager = LinearLayoutManager(this@NotificationActivity)
    }

    private fun setButton() = with(binding) {
        ivNotifyBack.setOnClickListener {
            finish()
        }
    }

    private fun initViewModel() = with(viewmodel) {
        lifecycleScope.launch {
            notifyItem.collect {
                when(it) {
                    is Loading -> showProgressOverlay()
                    is Success -> {
                        hideProgressOverlay()
                        adapter.submitList(it.data)
                    }
                    is Error -> {
                        hideProgressOverlay()
                        binding.root.setSnackBar(SnackType.Error, "${it.throwable.message}")
                    }
                }
            }
        }
    }
}