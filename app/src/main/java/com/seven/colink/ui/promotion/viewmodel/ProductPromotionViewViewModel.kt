package com.seven.colink.ui.promotion.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.search.helper.toUserID
import com.seven.colink.R
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import com.seven.colink.ui.promotion.model.ViewLinkImg
import com.seven.colink.util.status.DataResultStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductPromotionViewViewModel @Inject constructor(
    private val productRepository : ProductRepository,
    private val userRepository: UserRepository,
    private val context: Application,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _product = MutableLiveData<ProductEntity?>()
    val product: LiveData<ProductEntity?> get() = _product
    private val _currentUserId = MutableLiveData<String>()
    val currentUserId : LiveData<String> get() = _currentUserId
    private val _result = MutableLiveData<DataResultStatus>()
    val result : LiveData<DataResultStatus> get() = _result
    private val _setLeader = MutableLiveData<Result<UserEntity?>?>()
    val setLeader : LiveData<Result<UserEntity?>?> get() = _setLeader
    private val _setView = MutableLiveData<MutableList<ProductPromotionItems>>()
    val setView : LiveData<MutableList<ProductPromotionItems>> get() = _setView
    private val _setMember = MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>>()
    val setMember : MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>> get() = _setMember
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var entity : ProductEntity? = null
    private var userEntity = UserEntity()
    private val _key = MutableLiveData<String?>()
    val key : LiveData<String?> get() =_key


    fun initProduct(keyValue: String) {
        getData(keyValue)
    }

    private fun getData(key: String) {
        _isLoading.value = true
        entity = ProductEntity()
        var memberList = mutableListOf<ProductPromotionItems.ProjectMember>()

        viewModelScope.launch {
            memberList.clear()
            val repository = productRepository.getProductDetail(key)
            val getEntity = repository.getOrNull()
            entity = getEntity?.memberIds?.let { mem ->
                entity?.copy(
                    key = key,
                    authId = getEntity.authId,
                    memberIds = mem,
                    title = getEntity.title,
                    imageUrl = getEntity.imageUrl,
                    description = getEntity.description,
                    desImg = getEntity.desImg,
                    tags = getEntity.tags,
                    registeredDate = getEntity.registeredDate,
                    referenceUrl = getEntity.referenceUrl,
                    aosUrl = getEntity.aosUrl,
                    iosUrl = getEntity.iosUrl
                    )
            }

            val getLeaderDetail = getEntity?.authId?.let { userRepository.getUserDetails(it) }

            val memberIds = getEntity?.memberIds
            if (memberIds != null) {
                var memberDetailList = mutableListOf<ProductPromotionItems.ProjectMember>()
                memberIds.forEach { id ->
                    val detail = userRepository.getUserDetails(id)
                    val userNt = detail.getOrNull()
                    val user = userEntity.copy(
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
                    this@ProductPromotionViewViewModel.userEntity = user
                    memberDetailList.add(ProductPromotionItems.ProjectMember(user))
                }
                memberList = memberDetailList
            }
            _setLeader.value = getLeaderDetail
            _setMember.value = memberList
            _product.value = entity
            _isLoading.value = false
        }
    }

    private fun getString(stringKey : Int) : String {
        return context.getString(stringKey)
    }

    fun getViewList(): MutableList<ProductPromotionItems> {
        val viewList = mutableListOf<ProductPromotionItems>()
        val item = _product.value
        viewList.addAll(
            listOf(
                ProductPromotionItems.Img(item?.imageUrl),
                ProductPromotionItems.Title(item?.title, item?.registeredDate, item?.description,item?.referenceUrl,item?.aosUrl,item?.iosUrl,item?.teamId),
                ProductPromotionItems.MiddleImg(item?.desImg),
                ProductPromotionItems.Link(item?.referenceUrl, item?.iosUrl, item?.aosUrl),
                ProductPromotionItems.ProjectHeader(getString(R.string.product_header)),
                ProductPromotionItems.ProjectLeaderHeader(getString(R.string.product_leader)),
                ProductPromotionItems.ProjectLeaderItem(_setLeader.value),
                ProductPromotionItems.ProjectMemberHeader(getString(R.string.product_member)),
            )
        )
        _setMember.value?.forEach { member ->
            viewList.add(member)
        }
        return viewList
    }

    fun getIdCompareAuthId() {
        viewModelScope.launch {
            val current = authRepository.getCurrentUser()
            _currentUserId.postValue(current.message)
            val leaderId = _setLeader.value?.getOrNull()?.uid
            val currentId = current.message

            if (leaderId != null && leaderId == currentId) {
                _result.postValue(DataResultStatus.SUCCESS)
            }
        }
    }

    fun setProductKey() {
        _key.value = _product.value?.key
    }
}