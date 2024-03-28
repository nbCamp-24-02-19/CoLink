package com.seven.colink.ui.post.content.viewmodel

import android.app.Application
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
    private val _uiState = MutableLiveData<List<PostContentItem>?>()
    val uiState: LiveData<List<PostContentItem>?> get() = _uiState

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

    private var _currentUser: UserEntity? = null
    private val currentUser get() = _currentUser

    init {
        viewModelScope.launch {
            _currentUser = authRepository.getCurrentUser().message.let {
                userRepository.getUserDetails(it)
            }.getOrNull()
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
//해당 댓글의 키값 가져오기...registerComment는 댓글 작성하는 것,,getComment는 댓글 가져오는 것..
    //근데 댓글...음?..음???? 그 가져온 댓글에서 description만 수정하기..ㅋ.ㅋ..가능?ㅎㅎ
    //getComment는 suspend를 사용해야 사용 가능 그럼 이걸 사용하는 것이 아닌듯

    fun editComment(key: String, comment: String){
        viewModelScope.launch {
            commentRepository.editComment(key, comment)
        }
        setPostContentItems(entity.recruit)
    }

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
                        recruitInfo = entity.recruitInfo,
                        startDate = entity.startDate,
                        endDate = entity.endDate
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
                createMember(currentEntity).let { memberItems ->
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
                checkLike()
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

    suspend fun applyForProject(recruitItem: PostContentItem.RecruitItem) {
        val newApplicationInfo = ApplicationInfo(
            userId = getCurrentUser(),
            applicationStatus = ApplicationStatus.PENDING,
        )
        updateRecruitList(recruitItem, newApplicationInfo)
        sendNotificationApplyUseCase(entity)
    }

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
        like = like,
        isLike = isLike
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

    fun discernLike(key: String){
        viewModelScope.launch {
            if (currentUser?.likeList?.contains(key) == false){
                _currentUser = currentUser!!.copy(likeList = currentUser!!.likeList?.plus(listOf(key)))
                val updateUiState = _uiState.value?.map { item ->
                    if (item is PostContentItem.Item && item.key == entity.key){
                        item.copy(like = item.like?.plus(1), isLike = true)
                    } else {
                        item
                    }
                }
                _uiState.value = updateUiState
            } else {
                _currentUser = currentUser!!.copy(likeList = currentUser!!.likeList?.minus(listOf(key).toSet()))
                val updateUiState = _uiState.value?.map { item ->
                    if (item is PostContentItem.Item && item.key == entity.key){
                        item.copy(like = item.like?.minus(1), isLike = false)
                    } else {
                        item
                    }
                }
                _uiState.value = updateUiState
            }
        }
    }

    private fun checkLike(){
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            val checkLike = userRepository.getUserDetails(currentUser.message)
            val isLikeCheck = checkLike.getOrNull()?.likeList?.contains(entity.key) ?: false
            val getPost = postRepository.getPost(entity.key).getOrNull()?.like
            val updateUiState = _uiState.value?.map { item ->
                if (item is PostContentItem.Item && item.key == entity.key){
                    item.copy(isLike = isLikeCheck, like = getPost)
                } else {
                    item
                }
            }
            _uiState.value = updateUiState
        }
    }

    fun updateUserInfo(){
        viewModelScope.launch {
            val userLikeList = _currentUser
            if (userLikeList != null) {
                userRepository.updateUserInfo(userLikeList)
            }
        }
    }

    fun updatePostLike(){
        viewModelScope.launch {
             _uiState.value?.map { item ->
                if (item is PostContentItem.Item && item.key == entity.key){
                    item.like?.let { postRepository.registerLike(item.key, item.like) }
                } else {
                    item
                }
            }
        }
    }
}
