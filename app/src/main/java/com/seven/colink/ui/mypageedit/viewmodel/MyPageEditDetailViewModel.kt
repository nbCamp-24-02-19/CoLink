package com.seven.colink.ui.mypageedit.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.mypageedit.model.MyPageEditModel
import com.seven.colink.util.status.SnackType
import com.seven.colink.util.status.UiState
import com.seven.colink.util.status.UiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class MyPageEditDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {
    private val _userDetail = MutableStateFlow<UiState<MyPageEditModel>>(Loading)
    val userDetail = _userDetail.asStateFlow()

    private val _uploadState = MutableSharedFlow<SnackType>()
    val uploadState = _uploadState.asSharedFlow()

    fun loadUserDetails() {
        viewModelScope.launch {
            _userDetail.value = Loading // 로딩 상태 설정
            try {
                val currentUser = authRepository.getCurrentUser().message
                val userDetails = userRepository.getUserDetails(currentUser).getOrNull()?.convert()
                if (userDetails != null) {
                    _userDetail.value = Success(userDetails)
                } else {
                    _userDetail.value = Error(Throwable("User details not found"))
                }
            } catch (e: Exception) {
                _userDetail.value = Error(e)
            }
        }
    }

    fun updateProfileImg(uri: Uri) {
        _userDetail.value = Success(
            (userDetail.value as Success<MyPageEditModel>).data.copy(
            selectUrl = uri
        ))
    }

    suspend fun update(text: String) {
        val data = (userDetail.value as Success<MyPageEditModel>).data
        val img = data.selectUrl?.let { imageRepository.uploadImage(it) }?.getOrNull()
        _userDetail.value = Loading
        try {
            userRepository.updateUserInfo(
                userRepository.getUserDetails(data.uid!!).getOrNull()?.copy(
                    name = text,
                    photoUrl = img?.toString()?: data.profileUrl
                )?: return
            )
            _uploadState.emit(
                SnackType.Success
            )
            _userDetail.value = Success(data.copy(name = text, profileUrl = data.profileUrl))
        } catch (e: Exception) {
            _userDetail.value = Error(e)
            _uploadState.emit(
                SnackType.Error
            )
        }
    }

    private fun UserEntity.convert() = MyPageEditModel(
        uid = uid,
        name = name,
        profileUrl = photoUrl,
    )
}
