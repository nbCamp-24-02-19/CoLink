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
    val notifyItem = _notifyList.asStateFlow()

    init {
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

    private fun NotificationEntity.convert() =
        when(type) {
            NotifyType.CHAT -> {
                NotifyItem.ChatItem(
                    key = key,
                    name = name,
                    registeredDate = registeredDate,
                    profileUrl = thumbnail,
                    description = message,
                )
            }
            else -> NotifyItem.DefaultItem(
                key = key,
                body = message,
                registeredDate = registeredDate,
                icon = getIconResByType(type),
                title = getTitleResByType(type),
                iconBackground = getIconBackgroundByType(type)
            )
        }

    private fun getIconResByType(type: NotifyType?) = when(type) {
        NotifyType.INVITE -> resourceRepository.getDrawable(R.drawable.ic_invite)
        else -> resourceRepository.getDrawable(R.drawable.ic_edit_mypage)
    }

    private fun getTitleResByType(type: NotifyType?) = when(type) {
        NotifyType.INVITE -> resourceRepository.getString(R.string.notify_new_invite)
        else -> resourceRepository.getString(R.string.notify_new_apply)
    }

    private fun getIconBackgroundByType(type: NotifyType?) = when(type) {
        NotifyType.INVITE -> resourceRepository.getColor(R.color.third_color)
        else -> resourceRepository.getColor(R.color.forth_color)
    }
}