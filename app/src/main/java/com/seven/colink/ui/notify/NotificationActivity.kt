package com.seven.colink.ui.notify

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.seven.colink.databinding.ActivityNotificationBinding
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.notify.adapter.NotificationAdapter
import com.seven.colink.ui.notify.viewmodel.NotificationViewModel
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.SnackType
import com.seven.colink.util.status.UiState.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll

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
                viewmodel.deleteNotify(it)
            },
            selectedFilter = { viewmodel.filterNotifications(it)},
            deleteAll = { viewmodel.deleteAll() }
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
            notifyList.collect {
                when(it) {
                    is Loading -> showProgressOverlay()
                    is Success -> {
                        hideProgressOverlay()
                        setList()
                    }
                    is Error -> {
                        hideProgressOverlay()
                        binding.root.setSnackBar(SnackType.Error, "${it.throwable.message}")
                    }
                }
            }
        }

        lifecycleScope.launch {
            observingList.collect {
                adapter.submitList(it)
            }
        }
    }
}