package com.seven.colink.ui.group.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupBoardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageRepository: ImageRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

}