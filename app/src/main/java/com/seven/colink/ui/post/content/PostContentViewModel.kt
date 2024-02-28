package com.seven.colink.ui.post.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.util.Constants
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {
    private val entity: PostEntity? by lazy {
        savedStateHandle.get<PostEntity>(Constants.EXTRA_POST_ENTITY)
    }
    private val groupType: GroupType? by lazy {
        savedStateHandle.get<GroupType>(Constants.EXTRA_GROUP_TYPE)
    }

    private val _postContentItems: MutableLiveData<List<PostContentItem>> = MutableLiveData()
    val postContentItems: LiveData<List<PostContentItem>> get() = _postContentItems

    init {
        updatePostContentItems()
    }

    private fun updatePostContentItems() {
        val items = mutableListOf<PostContentItem>()

        Log.d("TAG", "ViewModel ${createPostContentItem().tags}")

        items.add(createImageItem())
        createPostContentItem().let { items.add(it) }
        items.addAll(createPostRecruit())
        items.addAll(createMember())

        _postContentItems.value = items
    }

    private fun createPostContentItem() = PostContentItem.Item(
        id = entity?.key,
        groupType = groupType,
        title = entity?.title,
        tags = entity?.tags,
        datetime = entity?.registeredDate,
        content = entity?.description
    )

    private fun createImageItem() = PostContentItem.ImageItem(
        imageUrl = entity?.imageUrl.orEmpty()
    )

    private fun createPostRecruit() = entity?.recruit?.let { recruitList ->
        recruitList.map { recruitInfo ->
            PostContentItem.RecruitItem(recruit = recruitInfo)
        }
    } ?: emptyList()

    private fun createMember() = entity?.memberIds?.let { memberIds ->
        memberIds.map { memberId ->
            PostContentItem.MemberItem(userInfo = memberId)
        }
    } ?: emptyList()


}