package com.seven.colink.ui.promotion.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductPromotionEditViewModel @Inject constructor(
    private val context: Application,
    private val groupRepository: GroupRepository,
    private val productRepository : ProductRepository,
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {
    private lateinit var entity : ProductEntity

    private val _result = MutableLiveData<DataResultStatus>()
    private val _product = MutableLiveData<ProductEntity>()
    private val _setLeader = MutableLiveData<ProductPromotionItems.ProjectLeaderItem>()
    private val _setMember = MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _key = MutableLiveData<String>()
    private val _setProductLeader = MutableLiveData<Result<UserEntity?>?>()
    val setProductLeader : LiveData<Result<UserEntity?>?> get() = _setProductLeader
    val key : LiveData<String> get() = _key
    val result : LiveData<DataResultStatus> get() = _result
    val product : LiveData<ProductEntity> get() = _product
    val setLeader : LiveData<ProductPromotionItems.ProjectLeaderItem> get() = _setLeader
    val setMember : MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>> get() = _setMember
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun init(key: String) {
        if (key.startsWith("PRD")) {
            initProduct(key)
        }else {
            initPostToProduct(key)
        }
    }

    private fun initPostToProduct(key: String) {
        viewModelScope.launch {
            val ids = groupRepository.getGroupDetail(key)

            entity = ProductEntity(
                key = "PRD_" + UUID.randomUUID().toString(),
                projectId = "",
                authId = ids.getOrNull()?.authId ?: "",
                memberIds = ids.getOrNull()?.memberIds ?: emptyList(),
                title = "",
                imageUrl = "",
                description = "",
                desImg = "",
                postKey = ids.getOrNull()?.postKey,
                registeredDate = LocalDateTime.now().convertLocalDateTime(),
                referenceUrl = null,
                aosUrl = null,
                iosUrl = null
            )
            getMemberDetail(key)
        }
    }

    private fun initProduct(key: String) {
        viewModelScope.launch {
            getProductMemberDetail(key)
        }
    }

    private fun getMemberDetail(key: String) {
        var memberList = mutableListOf<ProductPromotionItems.ProjectMember>()

        viewModelScope.launch {
            val ids = groupRepository.getGroupDetail(key)

            val getLeaderDetail = ids.getOrNull()?.authId?.let { authId -> userRepository.getUserDetails(authId) }
            val setLeaderItem = ProductPromotionItems.ProjectLeaderItem(getLeaderDetail)

            entity = ProductEntity(authId = ids.getOrNull()?.authId)

            val memIds = ids.getOrNull()?.memberIds
            if (memIds != null) {
                var memberDetailList = mutableListOf<ProductPromotionItems.ProjectMember>()
                memIds.forEach { id ->
                    val detail = userRepository.getUserDetails(id)
                    val userNt = detail.getOrNull()
                    val user = UserEntity().copy(
                        uid = userNt?.uid,
                        name = userNt?.name,
                        photoUrl = userNt?.photoUrl,
                        level = userNt?.level,
                        grade = userNt?.grade,
                        skill = userNt?.skill,
                        info = userNt?.info,
                        participantsChatRoomIds = userNt?.participantsChatRoomIds,
                        chatRoomKeyList = userNt?.chatRoomKeyList
                    )

                    memberDetailList.add(ProductPromotionItems.ProjectMember(user))
                    val delAuth = ids.getOrNull()?.authId
                    val delList = memberDetailList.filterNot { member ->
                        member.userInfo?.uid == delAuth
                    }
                    val list = mutableListOf<String>()
                    delList.forEach {
                        it.userInfo?.uid?.let { uid -> list.add(uid) }
                    }
                    memberDetailList = delList.toMutableList()
                    entity = entity.copy(memberIds = list, postKey = key)

                }
                val setMemberItem = memberDetailList
                memberList.plus(setMemberItem)
                memberList = memberList.plus(setMemberItem).toMutableList()
            }
            _setMember.value = memberList
            _setLeader.value = setLeaderItem
        }
    }

    private fun getProductMemberDetail(key: String) {
        viewModelScope.launch {
            var memberList = mutableListOf<ProductPromotionItems.ProjectMember>()

            val detail = productRepository.getProductDetail(key).getOrNull()
            val authId = detail?.authId
            val memberId = detail?.memberIds
            entity = ProductEntity(
                key = key,
                authId = authId,
                memberIds = memberId,
                projectId = detail?.projectId,
                title = detail?.title,
                imageUrl = detail?.imageUrl,
                description = detail?.description,
                desImg = detail?.desImg,
                postKey = detail?.postKey,
                registeredDate = detail?.registeredDate,
                referenceUrl = detail?.referenceUrl,
                aosUrl = detail?.aosUrl,
                iosUrl = detail?.iosUrl
            )
            _product.value = entity

            val leaderDetail = detail?.authId?.let { userRepository.getUserDetails(it) }
            _setProductLeader.value = leaderDetail

            val leaderItem = ProductPromotionItems.ProjectLeaderItem(leaderDetail)
            _setLeader.value = leaderItem

            if (memberId != null) {
                val memberDetailList = mutableListOf<ProductPromotionItems.ProjectMember>()

                memberId.forEach { id ->
                    val detailMem = userRepository.getUserDetails(id)
                    val userNt = detailMem.getOrNull()
                    val user = UserEntity().copy(
                        uid = userNt?.uid,
                        name = userNt?.name,
                        photoUrl = userNt?.photoUrl,
                        level = userNt?.level,
                        grade = userNt?.grade,
                        skill = userNt?.skill,
                        info = userNt?.info,
                        participantsChatRoomIds = userNt?.participantsChatRoomIds,
                        chatRoomKeyList = userNt?.chatRoomKeyList
                    )

                    memberDetailList.add(ProductPromotionItems.ProjectMember(user))
                }
                memberList = memberDetailList
            }
            _setMember.value = memberList
        }
    }

    fun getViewList(): MutableList<ProductPromotionItems> {
        val viewList = mutableListOf<ProductPromotionItems>()
        val item = _product.value
        viewList.addAll(
            listOf(
                ProductPromotionItems.Img(item?.imageUrl),
                ProductPromotionItems.Title(item?.title, item?.registeredDate, item?.description,item?.projectId,item?.registeredDate,item?.aosUrl,item?.iosUrl),
                ProductPromotionItems.MiddleImg(item?.desImg),
                ProductPromotionItems.Link(item?.referenceUrl, item?.iosUrl, item?.aosUrl),
            )
        )
        return viewList
    }

    fun saveEntity(nt: ProductEntity) {
        entity = entity.copy(
            title = nt.title,
            imageUrl = nt.imageUrl,
            description = nt.description,
            projectId = nt.projectId,
            desImg = nt.desImg,
            referenceUrl = nt.referenceUrl,
            aosUrl = nt.aosUrl,
            iosUrl = nt.iosUrl
        )
    }

    suspend fun uploadImage(uri: Uri): String =
        imageRepository.uploadImage(uri).getOrThrow().toString()

    fun registerProduct()  {
        _product.value = entity
        saveProduct(entity)
        viewModelScope.launch {
            _result.value = productRepository.registerProduct(entity)
        }
        _key.value = entity.key
    }

    private fun saveProduct(nt : ProductEntity) {
        _product.value = nt
    }
}