package com.seven.colink.ui.notify

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seven.colink.databinding.ActivityNotificationBinding
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.group.GroupActivity
import com.seven.colink.ui.notify.adapter.NotificationAdapter
import com.seven.colink.ui.notify.viewmodel.FilterType
import com.seven.colink.ui.notify.viewmodel.FilterType.*
import com.seven.colink.ui.notify.viewmodel.NotificationViewModel
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.util.progress.hideProgressOverlay
import com.seven.colink.util.progress.showProgressOverlay
import com.seven.colink.util.snackbar.setSnackBar
import com.seven.colink.util.status.SnackType
import com.seven.colink.util.status.UiState.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
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
                    ChatRoomActivity.newIntent(this, it)
                )
                viewmodel.deleteNotify(it)
            },
            selectedFilter = { viewmodel.filterNotifications(it) },
            deleteAll = { viewmodel.deleteAll() },
            onGroup = {
                GroupActivity.newIntent(this, key = it)
                viewmodel.deleteNotify(it)
            },
            onPost = {
                PostActivity.newIntent(this, key = it)
                viewmodel.deleteNotify(it)
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
        rcNotifyList.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?) =
                if (holder is NotificationAdapter.FilterViewHolder) {
                    dispatchAddFinished(holder)
                    false
                } else super.animateAdd(holder)

            override fun animateRemove(holder: RecyclerView.ViewHolder?) =
                if (holder is NotificationAdapter.FilterViewHolder) {
                    dispatchRemoveFinished(holder)
                    false
                } else super.animateRemove(holder)
        }
    }

    private fun setButton() = with(binding) {
        ivNotifyBack.setOnClickListener {
            finish()
        }
    }

    private fun initViewModel() = with(viewmodel) {
        lifecycleScope.launch {
            combine(notifyList, currentFilter) { notifyList, currentFilter ->
                Pair(notifyList, currentFilter)
            }.collect { (notifyList, currentFilter) ->
                when (notifyList) {
                    is Loading -> showProgressOverlay()
                    is Success -> {
                        hideProgressOverlay()
                        adapter.submitList(
                            when (currentFilter) {
                                ALL -> listOf(NotifyItem.Filter(currentFilter)) + notifyList.data
                                CHAT -> listOf(NotifyItem.Filter(currentFilter)) + notifyList.data.filter { it !is NotifyItem.DefaultItem }
                                RECRUIT -> listOf(NotifyItem.Filter(currentFilter)) + notifyList.data.filter { it !is NotifyItem.ChatItem }
                            }
                        )
                    }

                    is Error -> {
                        hideProgressOverlay()
                        binding.root.setSnackBar(SnackType.Error, "${notifyList.throwable.message}")
                    }
                }
            }
        }
    }
}