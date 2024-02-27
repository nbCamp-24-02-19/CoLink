package com.seven.colink.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.R
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.util.Constants.Companion.EXTRA_ENTRY_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_GROUP_TYPE
import com.seven.colink.util.Constants.Companion.EXTRA_POST_ENTITY
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.PostEntryType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository
) : ViewModel() {
    private val entryType: PostEntryType? by lazy {
        savedStateHandle.get<PostEntryType>(EXTRA_ENTRY_TYPE)
    }
    private val entity: PostEntity? by lazy {
        savedStateHandle.get<PostEntity>(EXTRA_POST_ENTITY)
    }

    private val groupType: GroupType? by lazy {
        savedStateHandle.get<GroupType>(EXTRA_GROUP_TYPE)
    }

    private val _uiState: MutableLiveData<PostViewState> = MutableLiveData(PostViewState.init())
    val uiState: LiveData<PostViewState> get() = _uiState

    init {
        initViewState()
    }

    private fun initViewState() {
        _uiState.value = when (groupType) {
            GroupType.PROJECT -> PostViewState.init().copy(
                isProjectSelected = true,
                projectButtonTextColor = R.color.white,
                studyButtonTextColor = R.color.main_color,
                editTextContent = R.string.input_content_project
            )

            GroupType.STUDY -> PostViewState.init().copy(
                isProjectSelected = false,
                projectButtonTextColor = R.color.main_color,
                studyButtonTextColor = R.color.white,
                editTextContent = R.string.input_content_study
            )

            else -> PostViewState.init()
        }
    }
}