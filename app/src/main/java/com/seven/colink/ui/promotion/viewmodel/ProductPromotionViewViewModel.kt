package com.seven.colink.ui.promotion.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.promotion.model.ProductPromotionItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductPromotionViewViewModel @Inject constructor(
    private val productRepository : ProductRepository,
    private val userRepository: UserRepository,
    private val context: Application
) : ViewModel() {

    private val _product = MutableLiveData<ProductEntity?>()
    val product: LiveData<ProductEntity?> get() = _product
    private val _isLoading = MutableLiveData<Boolean>()
//    private val _setLeader = MutableLiveData<ProductPromotionItems.ProjectLeaderItem>()
    private val _setLeader = MutableLiveData<Result<UserEntity?>?>()
    private val _setMember = MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>>()
    private val _setView = MutableLiveData<MutableList<ProductPromotionItems>>()
    val setView : LiveData<MutableList<ProductPromotionItems>> get() = _setView
    val setLeader : LiveData<Result<UserEntity?>?> get() = _setLeader
    val setMember : MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>> get() = _setMember
    val isLoading: LiveData<Boolean> get() = _isLoading
    private var entity : ProductEntity? = null
    private var userEntity = UserEntity()

    fun initProduct(key: String) {
        _isLoading.value = true
        Handler(Looper.getMainLooper()).postDelayed( {getData(key)} , 2000)
        _isLoading.value = false
//        getData(key)
    }

    private fun getData(key: String) {

        entity = ProductEntity()
        val authEntity = UserEntity()
        var memberList = mutableListOf<ProductPromotionItems.ProjectMember>()

        viewModelScope.launch {
            memberList.clear()
            val repository = productRepository.getProductDetail(key)
            val getEntity = repository.getOrNull()
            Log.d("ViewModelView","#aaa getEntity = $getEntity")
//            entity = getEntity
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
            Log.d("ViewModelView","#aaa entity = $entity")

//            val getLeaderDetail = getEntity?.authId?.let { getMemberDetail(it) }
            val getLeaderDetail = getEntity?.authId?.let { userRepository.getUserDetails(it) }
            val setLeaderItem = ProductPromotionItems.ProjectLeaderItem(getLeaderDetail)
            Log.d("ViewModelView","#aaa getLeader = $getLeaderDetail")
            Log.d("ViewModelView","#aaa setLeader = $setLeaderItem")

            val memberIds = getEntity?.memberIds
            if (memberIds != null) {
                var memberDetailList = mutableListOf<ProductPromotionItems.ProjectMember>()
                memberIds.forEach { id ->
//                    val detail = getMemberDetail(id)
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
                    Log.d("ViewModelView","#aaa userEntity = $userEntity")
                    Log.d("ViewModelView","#aaa user = $user")
                    memberDetailList.add(ProductPromotionItems.ProjectMember(user))
                    Log.d("ViewModelView","#aaa memberDetailList = $memberDetailList")
                }
                memberList = memberDetailList
            }
            _setLeader.value = getLeaderDetail
            _setMember.value = memberList
            _product.value = entity

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
                ProductPromotionItems.Title(item?.title, item?.registeredDate, item?.description),
                ProductPromotionItems.MiddleImg(item?.desImg),
                ProductPromotionItems.Link(item?.referenceUrl, item?.iosUrl, item?.aosUrl),
                ProductPromotionItems.ProjectHeader(getString(R.string.product_header)),
                ProductPromotionItems.ProjectLeaderHeader(getString(R.string.product_leader)),
                ProductPromotionItems.ProjectLeaderItem(_setLeader.value),
                ProductPromotionItems.ProjectMemberHeader(getString(R.string.product_member)),
//                ProductPromotionItems.ProjectMember(userEntity)
            )
        )
        _setMember.value?.forEach { member ->
            viewList.add(member)
        }
        Log.d("ViewModelView","#aaa list = $viewList")
        return viewList
    }
}