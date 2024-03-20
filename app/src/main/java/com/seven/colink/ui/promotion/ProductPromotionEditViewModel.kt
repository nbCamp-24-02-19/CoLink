package com.seven.colink.ui.promotion

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.util.convert.convertLocalDateTime
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
    private val userRepository: UserRepository
) : ViewModel() {
//    var entity : ProductEntity? = null
    var entity = ProductEntity()

    private val _product = MutableLiveData<ProductEntity>()
    private val _setView = MutableLiveData<ProductPromotionItems>()
    private val _setLeader = MutableLiveData<ProductPromotionItems.ProjectLeaderItem>()
    private val _setMember = MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>>()
    val product : LiveData<ProductEntity> get() = _product
    val setView : LiveData<ProductPromotionItems> get() = _setView
    val setLeader : LiveData<ProductPromotionItems.ProjectLeaderItem> get() = _setLeader
    val setMember : MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>> get() = _setMember

    private val _adapterData = MutableLiveData<ProductPromotionItems?>()
    val adapterData: LiveData<ProductPromotionItems?> get() = _adapterData


    fun init(key: String) {
        if (entity.title?.isEmpty() == true) {
            initPostToProduct(key)
        }else {
            initProduct(key)
        }
    }

    private fun updateAdapterData(data: ProductPromotionItems) {
        _adapterData.value = data
    }

    private fun initPostToProduct(key: String) {  //포스트에서 프로덕트로 만들때
        entity = ProductEntity(
            key = "PRD_" + UUID.randomUUID().toString(),
            projectId = "",
            authId = "",
            memberIds = emptyList(),
            title = "",
            imageUrl = "",
            description = "",
            desImg = "",
            tags = emptyList(),
            registeredDate = LocalDateTime.now().convertLocalDateTime(),
            referenceUrl = null,
            aosUrl = null,
            iosUrl = null
        )
//        _product.value = entity
        getMemberDetail(key)
    }

    private fun initProduct(key: String) {  //프로덕트를 편집할때
        viewModelScope.launch {
            val id = productRepository.getProductDetail(key)
            id.onSuccess {
                _product.value = it
            }
        }
    }

    fun getMemberDetail(key: String) {
        val viewList = mutableListOf<ProductEntity>()
        var memberList = mutableListOf<ProductPromotionItems.ProjectMember>()

        viewModelScope.launch {
            val ids = groupRepository.getGroupDetail(key)

            val viewItem = ids.getOrNull()?.memberIds?.let { member -> entity?.copy(authId = ids.getOrNull()?.authId, memberIds = member) }
            Log.d("Viewmodel","#aaa postUser Id = ${ids.getOrNull()?.authId}")
            Log.d("Viewmodel","#aaa postMember Id = ${ids.getOrNull()?.memberIds}")
            if (viewItem != null) {
                viewList.add(viewItem)
            }
            val getLeaderDetail = ids.getOrNull()?.authId?.let { authId -> userRepository.getUserDetails(authId) }
            val setLeaderItem = ProductPromotionItems.ProjectLeaderItem(getLeaderDetail)

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
                    entity = ProductEntity(authId = user.uid)

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
                    entity = entity.copy(memberIds = list)

                }
                    val setMemberItem = memberDetailList
                    Log.d("Viewmodel","#aaa 디테일 = $memberDetailList")
                    Log.d("Viewmodel","#aaa set 아이템 = $setMemberItem")
                    memberList.plus(setMemberItem)
                    Log.d("Viewmodel","#aaa 멤버리스트 = $setMemberItem")
                memberList = memberList.plus(setMemberItem).toMutableList()
                }
            _setMember.value = memberList
            _setLeader.value = setLeaderItem
            }
        }

    fun saveEntity(nt: ProductEntity) {
        entity = entity.copy(
            title = nt.title,
            imageUrl = nt.imageUrl,
            description = nt.description,
            desImg = nt.desImg,
            referenceUrl = nt.referenceUrl,
            aosUrl = nt.aosUrl,
            iosUrl = nt.iosUrl
        )
        Log.d("Edit","#ccc viewModel save entity = $entity")
    }

    fun registerProduct() {
        _product.value = entity

        saveProduct(entity)
        Log.d("ViewModel","#bbb save entity = $entity")
        viewModelScope.launch {
            productRepository.registerProduct(entity)
            Log.d("ViewModel","#bbb firebase entity = $entity")
        }
    }

    private fun saveProduct(nt : ProductEntity) {
        _product.value = nt
    }
}