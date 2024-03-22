package com.seven.colink.ui.post.content.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.CommentEntity
import com.seven.colink.domain.entity.RecruitInfo
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.CommentRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.domain.usecase.GetPostUseCase
import com.seven.colink.domain.usecase.RegisterApplicationInfoUseCase
import com.seven.colink.domain.usecase.SendNotificationApplyUseCase
import com.seven.colink.ui.group.board.board.GroupContentViewType
import com.seven.colink.ui.post.content.model.ContentButtonUiState
import com.seven.colink.ui.post.content.model.DialogUiState
import com.seven.colink.ui.post.content.model.PostContentItem
import com.seven.colink.ui.post.register.post.model.PostErrorMessage
import com.seven.colink.ui.post.register.post.model.PostErrorUiState
import com.seven.colink.ui.post.register.post.model.Post
import com.seven.colink.util.convert.convertTime
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostContentViewModel @Inject constructor(
    private val context: Application,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val getPostUseCase: GetPostUseCase,
    private val registerApplicationInfoUseCase: RegisterApplicationInfoUseCase,
    private val commentRepository: CommentRepository,
    private val sendNotificationApplyUseCase: SendNotificationApplyUseCase,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private lateinit var entity: Post
    private val _uiState = MutableLiveData<List<PostContentItem>>()
    val uiState: LiveData<List<PostContentItem>> get() = _uiState

    private val _dialogUiState = MutableLiveData(
        DialogUiState.init()
    )
    val dialogUiState: LiveData<DialogUiState> get() = _dialogUiState

    private val _updateButtonUiState = MutableLiveData<ContentButtonUiState>()
    val updateButtonUiState: LiveData<ContentButtonUiState> get() = _updateButtonUiState
    private val _errorUiState: MutableLiveData<PostErrorUiState> =
        MutableLiveData(PostErrorUiState.init())
    val errorUiState: LiveData<PostErrorUiState> get() = _errorUiState

    private val _userComment = MutableLiveData<CommentEntity>()
    val userComments: LiveData<CommentEntity> = _userComment


    private val _checkLogin = MutableLiveData<Boolean>(false)
    val checkLogin: LiveData<Boolean> get() = _checkLogin

    private val _isLike = MutableLiveData<Boolean>()
    val isLike: LiveData<Boolean> get() = _isLike

    private var _currentUser: UserEntity? = null
    private val currentUser get() = _currentUser

    private val _likeList = MutableLiveData<List<String>?>()
    val likeList: LiveData<List<String>?> get() = _likeList

    init {
        viewModelScope.launch {
            _currentUser = authRepository.getCurrentUser().message.let {
                userRepository.getUserDetails(it)
            }.getOrNull()

//            _likeList.value = authRepository.getCurrentUser().message.let {
//                userRepository.getUserDetails(it)
//            }.getOrNull()?.likeList
        }
    }

    suspend fun setEntity(key: String) {
        entity = getPostUseCase(key) ?: return
    }

    fun initViewStateByEntity() = viewModelScope.launch {
        setUserButtonUiState(entity)
        setPostContentItems(entity.recruit)
        incrementPostViews()
        getComment()
    }

    private suspend fun setUserButtonUiState(post: Post) {
        _updateButtonUiState.value = when (getCurrentUser()) {
            post.authId -> ContentButtonUiState.Manager
            null -> ContentButtonUiState.Unknown
            else -> ContentButtonUiState.User
        }
    }


    fun registerComment(text: String) {
        viewModelScope.launch {
            commentRepository.registerComment(
                getCurrentUser()?.let {
                    CommentEntity(
                        authId = it,
                        postId = entity.key,
                        description = text
                    )
                }?: return@launch
            )
        }
        setPostContentItems(entity.recruit)
    }
//수정을 하려면 일단 그 댓글의 키값을 가져와서 그 키값이랑 맞는 댓글의 description을 수정해야 하는것이지?????????????????????
    //근데???????????밑에 이 친구는 그냥 댓글 추가 하는 거잖아???????????????ㅎ..ㅋ.ㅎ..ㅎ.ㅎ..ㅎ.
//    fun editComment(text: String) {
//        viewModelScope.launch {
//            commentRepository.registerComment(
//                getCurrentUser()?.let {
//                    CommentEntity(
////                        key = ,
//                        authId = it,
//                        postId = entity.key,
//                        description = text
//                    )
//                }?: return@launch
//            )
//        }
//        setPostContentItems(entity.recruit)
//    }

    fun deleteComment(key: String){
        viewModelScope.launch {
            commentRepository.deleteComment(key)
        }
        setPostContentItems(entity.recruit)
    }
    private suspend fun getComment() =
            commentRepository.getComment(
                postId = entity.key
            ).getOrNull()?.sortedBy { it.registeredDate }

    private fun setPostContentItems(updatedRecruitList: List<RecruitInfo>?) =
        viewModelScope.launch {
            val items = mutableListOf<PostContentItem>()
            entity.let { currentEntity ->
                items.add(currentEntity.createPostContentItem())
                items.add(
                    PostContentItem.AdditionalInfo(
                        key = entity.key,
                        precautions = entity.precautions,
                        recruitInfo = entity.recruitInfo
                    )
                )
                items.add(
                    PostContentItem.TitleItem(
                        R.string.recruitment_status,
                        GroupContentViewType.MEMBER_ITEM
                    )
                )
                val recruitItems = createPostRecruit(updatedRecruitList)
                if (recruitItems.isEmpty()) {
                    items.add(PostContentItem.MessageItem(context.getString(R.string.no_recruitment_status)))
                } else {
                    items.addAll(recruitItems)
                }
                items.add(
                    PostContentItem.TitleItem(
                        if (currentEntity.groupType == GroupType.PROJECT) R.string.project_member_info else R.string.study_member_info,
                        GroupContentViewType.UNKNOWN
                    )
                )
                createMember(currentEntity)?.let { memberItems ->
                    items.addAll(memberItems)
                }
                items.add(PostContentItem.CommentTitle(R.string.comment))
                getComment()?.forEach {
                    userRepository.getUserDetails(it.authId).getOrNull().let {user ->
                        items.add(
                            PostContentItem.CommentItem(
                                key = it.key,
                                name = user?.name?:"",
                                profile = user?.photoUrl?: "",
                                description = it.description,
                                registeredDate = it.registeredDate.convertTime(),
                                authId = it.authId,
                                buttonUiState = it.authId == getCurrentUser()
                            )
                        )
                    }
                }
                items.add(
                    PostContentItem.CommentSendItem
                )
                _uiState.value = items
            }
        }

    private fun createPostRecruit(recruitList: List<RecruitInfo>?) =
        recruitList?.map { recruitInfo ->
            PostContentItem.RecruitItem(
                key = entity.key,
                recruit = recruitInfo,
                buttonUiState = updateButtonUiState.value ?: ContentButtonUiState.User
            )
        } ?: emptyList()

    private suspend fun createMember(uiState: Post): List<PostContentItem> {
        val group = groupRepository.getGroupDetail(uiState.key).getOrNull()
        val memberItems = mutableListOf<PostContentItem>()
        var leaderTitleAdded = false
        val memberIdsSet = group?.memberIds?.toSet()

        if (memberIdsSet != null) {
            for (memberId in memberIdsSet) {
                val userEntity = userRepository.getUserDetails(memberId).getOrNull()

                if (userEntity != null) {
                    val isLeader = uiState.authId == memberId

                    if (isLeader && !leaderTitleAdded) {
                        memberItems.add(PostContentItem.SubTitleItem(R.string.project_team_leader))
                    } else if (!isLeader && !leaderTitleAdded) {
                        memberItems.add(PostContentItem.SubTitleItem(R.string.project_team_member))
                        leaderTitleAdded = true
                    }

                    memberItems.add(
                        PostContentItem.MemberItem(
                            key = uiState.key,
                            userInfo = userEntity
                        )
                    )
                }
            }
        }

        return memberItems
    }

    private suspend fun getCurrentUser(): String? {
        return authRepository.getCurrentUser().let {
            if (it == DataResultStatus.SUCCESS) it.message else null
        }
    }

    // 모집 분야 지원 했을 때
    suspend fun applyForProject(recruitItem: PostContentItem.RecruitItem) {
        val newApplicationInfo = ApplicationInfo(
            userId = getCurrentUser(),
            applicationStatus = ApplicationStatus.PENDING,
        )
        updateRecruitList(recruitItem, newApplicationInfo)
        sendNotificationApplyUseCase(entity)
    }

    // 지원한 회원 데이터 추가
    private suspend fun updateRecruitList(
        recruitItem: PostContentItem.RecruitItem,
        newApplicationInfo: ApplicationInfo
    ) {
        val isAlreadySupportedResult = isAlreadySupported(recruitItem)
        if (isAlreadySupportedResult == PostErrorMessage.ALREADY_SUPPORT) {
            _errorUiState.value =
                errorUiState.value?.copy(message = PostErrorMessage.ALREADY_SUPPORT)
        } else {
            entity.recruit?.map { recruitInfo ->
                if (recruitInfo.type == recruitItem.recruit.type) {
                    val newItem = ApplicationInfo(
                        key = newApplicationInfo.key,
                        recruitId = recruitItem.recruit.key,
                        userId = newApplicationInfo.userId,
                        applicationStatus = newApplicationInfo.applicationStatus,
                        applicationDate = newApplicationInfo.applicationDate
                    )

                    registerApplicationInfoUseCase.invoke(newItem)

                    entity = entity.copy(
                        recruit = entity.recruit?.map { existingRecruitInfo ->
                            if (existingRecruitInfo.type == recruitItem.recruit.type) {
                                existingRecruitInfo.copy(
                                    applicationInfos = existingRecruitInfo.applicationInfos.orEmpty() + newItem
                                )
                            } else {
                                existingRecruitInfo
                            }
                        }
                    )
                }
            }
            _errorUiState.value =
                errorUiState.value?.copy(message = PostErrorMessage.SUCCESS_SUPPORT)
            setPostContentItems(entity.recruit)
        }
    }


    // 이미 지원 한 회원일 때
    private suspend fun isAlreadySupported(recruitItem: PostContentItem.RecruitItem): PostErrorMessage {
        val isAlreadySupported = entity.recruit?.any { recruitInfo ->
            recruitInfo.type == recruitItem.recruit.type &&
                    recruitInfo.applicationInfos?.any { it.userId == getCurrentUser() } == true
        } == true

        return if (isAlreadySupported) {
            PostErrorMessage.ALREADY_SUPPORT
        } else {
            PostErrorMessage.SUCCESS_SUPPORT
        }
    }

    // 조회수
    private suspend fun incrementPostViews(): DataResultStatus =
        postRepository.incrementPostViews(entity.key)

    private fun Post.createPostContentItem() = PostContentItem.Item(
        key = key,
        authId = authId,
        title = title,
        status = status,
        imageUrl = imageUrl.orEmpty(),
        groupType = groupType,
        description = description,
        tags = tags,
        registeredDate = registeredDate,
        views = views,
        like = like
    )

    fun createDialog(recruitItem: PostContentItem.RecruitItem) {
        _dialogUiState.value = dialogUiState.value?.copy(
            title = if (entity.groupType == GroupType.PROJECT) context.getString(R.string.project_kor) else context.getString(
                R.string.study_kor
            ),
            message = entity.title,
            groupType = entity.groupType,
            recruitItem = recruitItem
        )
    }

    fun checkLogin(){
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _checkLogin.value = currentUser == DataResultStatus.SUCCESS
        }
    }

    fun discernLike(key: String) : Boolean? {
        viewModelScope.launch {
            Log.d("Post", "@@@ LikeList = ${currentUser?.likeList}")

            if (currentUser?.likeList?.contains(key) == false){
                _currentUser = currentUser!!.copy(likeList = currentUser!!.likeList?.plus(listOf(key)))
                _isLike.value = true
                Log.d("Post","!!! likeList containsNot key")
                Log.d("Post","likeList = ${currentUser?.likeList}")
            } else {
                _currentUser = currentUser!!.copy(likeList = currentUser!!.likeList?.minus(listOf(key).toSet()))
                _isLike.value = false
                Log.d("Post","### likeList contains key")
                Log.d("Post","likeList = ${currentUser?.likeList}")
            }
            Log.d("Post","_isLike.value = ${_isLike.value}")
        }
        Log.d("Post","isLike.value = ${isLike.value}")
        return _isLike.value
    }

//    fun discernLike(key: String) {
//        viewModelScope.launch {
//            Log.d("Post", "@@@ LikeList = ${likeList.value}")
//
//            val currentList = likeList.value ?: emptyList()
//
//            if (!currentList.contains(key)){
//                _likeList.value = currentList.plus(key)
//                _isLike.value = true
//                Log.d("Post","!!! likeList containsNot key")
//                Log.d("Post","likeList = ${likeList}")
//            } else {
//                _likeList.value = currentList.minus(key)
//                _isLike.value = false
//                Log.d("Post","### likeList contains key")
//                Log.d("Post","likeList = ${likeList}")
//            }
//        }
//    }

}
