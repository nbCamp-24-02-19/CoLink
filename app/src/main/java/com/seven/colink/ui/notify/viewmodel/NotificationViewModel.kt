package com.seven.colink.ui.notify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.NotificationEntity
import com.seven.colink.domain.model.NotifyType
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.NotificationStoreRepository
import com.seven.colink.domain.repository.ResourceRepository
import com.seven.colink.ui.notify.NotifyItem
import com.seven.colink.ui.notify.viewmodel.NotificationViewModel.FilterType.*
import com.seven.colink.util.convert.convertTime
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationStoreRepository: NotificationStoreRepository,
    private val resourceRepository: ResourceRepository,
): ViewModel() {
    private val _notifyList = MutableStateFlow<UiState<List<NotifyItem>>>(UiState.Loading)
    val notifyList = _notifyList.asStateFlow()

    private val _observingList = MutableStateFlow<List<NotifyItem>>(emptyList())
    val observingList = _observingList.asStateFlow()

    private var _currentFilter = ALL
    private val currentFilter get() = _currentFilter

    init {
        setNotify()
    }

    private fun setNotify() {
        viewModelScope.launch {
            _notifyList.value =
                try {
                    notificationStoreRepository.getNotificationByUid(
                        authRepository.getCurrentUser().message
                    ).map {
                        it.convert()
                    }.let {
                        UiState.Success(listOf(NotifyItem.Filter) + it)
                    }
                } catch (e: Exception) {
                    UiState.Error(e)
                }
        }
    }

    fun setList(){
        val list = notifyList.value
        list as UiState.Success
        _observingList.value = when (currentFilter) {
            ALL -> list.data
            CHAT -> list.data.filterIsInstance<NotifyItem.ChatItem>()
            RECRUIT -> list.data.filterIsInstance<NotifyItem.DefaultItem>()
        }
    }
    fun filterNotifications(filterType: FilterType) {
        _currentFilter = filterType
        setList()
    }
    private fun NotificationEntity.convert() =
        when(type) {
            NotifyType.CHAT -> {
                NotifyItem.ChatItem(
                    key = key,
                    name = name,
                    registeredDate = registeredDate.convertTime(),
                    profileUrl = thumbnail,
                    description = message,
                )
            }
            else -> NotifyItem.DefaultItem(
                key = key,
                body = message,
                registeredDate = registeredDate.convertTime(),
                icon = getIconResByType(type),
                title = getTitleResByType(type),
                iconBackground = getIconBackgroundByType(type)
            )
        }

    fun deleteNotify(key: String) {
        viewModelScope.launch {
            notificationStoreRepository.deleteNotification(key)
            setNotify()
        }
    }

    fun deleteAll() {
        val list = notifyList.value
        list as UiState.Success
        list.data.forEach {
            when(it) {
                is NotifyItem.ChatItem -> deleteNotify(it.key!!)
                is NotifyItem.DefaultItem -> deleteNotify(it.key!!)
                else -> Unit
            }
        }
        setNotify()
    }

    private fun getIconResByType(type: NotifyType?) = when(type) {
        NotifyType.INVITE -> resourceRepository.getDrawable(R.drawable.ic_invite)
        NotifyType.APPLY -> resourceRepository.getDrawable(R.drawable.ic_apply_request)
        NotifyType.JOIN -> resourceRepository.getDrawable(R.drawable.ic_join)
        else -> null
    }

    private fun getTitleResByType(type: NotifyType?) = when(type) {
        NotifyType.INVITE -> resourceRepository.getString(R.string.notify_new_invite)
        NotifyType.APPLY -> resourceRepository.getString(R.string.notify_new_apply)
        NotifyType.JOIN -> resourceRepository.getString(R.string.notify_join_group)
        else -> null
    }

    private fun getIconBackgroundByType(type: NotifyType?) = when(type) {
        NotifyType.INVITE -> resourceRepository.getColor(R.color.third_color)
        NotifyType.APPLY -> resourceRepository.getColor(R.color.sub_color)
        NotifyType.JOIN -> resourceRepository.getColor(R.color.forth_color)
        else -> null
    }

    enum class FilterType {
        ALL, CHAT, RECRUIT
    }
}