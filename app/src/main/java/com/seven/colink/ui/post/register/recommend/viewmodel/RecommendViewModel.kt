package com.seven.colink.ui.post.register.recommend.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetChatRoomUseCase
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.domain.usecase.SendNotificationInviteUseCase
import com.seven.colink.ui.post.register.recommend.type.RecommendType
import com.seven.colink.util.model.MemberCard
import com.seven.colink.util.model.MemberInfo
import com.seven.colink.util.status.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val chatRoomUseCase: GetChatRoomUseCase,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val recruitRepository: RecruitRepository,
    private val sendNotificationInviteUseCase: SendNotificationInviteUseCase,
) : ViewModel() {

    private val _chatRoomEvent = MutableSharedFlow<ChatRoomEntity>()
    val chatRoomEvent: SharedFlow<ChatRoomEntity> = _chatRoomEvent

    private val _recommendList = MutableStateFlow<UiState<List<RecommendType>>>(UiState.Loading)
    val recommendList: StateFlow<UiState<List<RecommendType>>> = _recommendList

    private var postEntity: PostEntity = PostEntity()
    fun loadList(key: String) {
        viewModelScope.launch {
            val titleDeferred = async {
                try {
                    userRepository.getUserDetails(
                        authRepository.getCurrentUser().message
                    ).getOrNull()?.name
                } catch (e: Exception) {
                    e
                }
            }

            val membersDeferred =
                key.let { postRepository.getPost(it) }.getOrNull()?.recruit?.map { recruitId ->
                    async {
                        recruitId.let { recruitRepository.getRecruit(it) }?.type?.let {type ->
                            userRepository.getUserBySpecialty(type).getOrNull()
                        }
                    }
                } ?: listOf()
            /*TODO 여기서 진행중인 프로젝트수로 추천 알고리즘 (점수제로 진행중인 프로젝트 하나당 -0.5점 같은 로직)*/

            val title = titleDeferred.await()
            val members = membersDeferred.awaitAll().mapNotNull {
                it
            }.flatten().sortedByDescending { it.grade }

            _recommendList.value = if (title is Exception) UiState.Error(title)
            else {
                try {
                    title as String
                    if (members.isNotEmpty()) {
                        UiState.Success(
                            listOfNotNull(
                                RecommendType.Title(title,key),
                                RecommendType.Card(members.first().convertCard()),
                                members.first().name?.let { RecommendType.Middle(it) },
                            ) + members.drop(1).map {
                                RecommendType.Others(it.convertUser())
                            } + listOf(RecommendType.Close)
                        )
                    } else {
                        UiState.Error(Exception("MembersEmpty"))
                    }
                } catch (e: Exception) {
                    UiState.Error(e)
                }
            }
            postRepository.getPost(key).getOrNull()?.also { postEntity = it }
        }
    }

    fun invitePost(uid: String) {
        viewModelScope.launch {
            sendNotificationInviteUseCase(postEntity, uid)
        }
    }

    fun setChatRoom(uid: String) {
        viewModelScope.launch {
            _chatRoomEvent.emit(chatRoomUseCase(uid))
        }
    }

    private fun UserEntity.convertCard() = MemberCard(
        key = uid,
        name = name,
        profileUrl = photoUrl,
        level = level,
        grade = grade,
        info = info,
        recruits = 1,
    )

    private fun UserEntity.convertUser() = MemberInfo(
        key = uid,
        name = name,
        profileUrl = photoUrl,
        info = info,
        grade = grade,
        level = level,
    )
}