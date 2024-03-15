package com.seven.colink.ui.promotion

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.R
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductPromotionEditViewModel @Inject constructor(
    private val context: Application,
    private val groupRepository: GroupRepository,
    private val productRepository : ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private lateinit var entity : ProductEntity

    private val _product = MutableLiveData<ProductEntity>()
    private val _setLeader = MutableLiveData<ProductPromotionItems.ProjectLeaderItem>()
    private val _setMember = MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>>()
    val product : LiveData<ProductEntity> get() = _product
    val setLeader : LiveData<ProductPromotionItems.ProjectLeaderItem> get() = _setLeader
    val setMember : MutableLiveData<MutableList<ProductPromotionItems.ProjectMember>> get() = _setMember


    fun init(key: String) {
        if (entity.title?.isNotEmpty() == true) {
            initPostToProduct(key)
        }else {
            initProduct(key)
        }
    }

    private fun initPostToProduct(key: String) {  //포스트에서 프로덕트로 만들때
        _product.value = entity
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

    private fun getMemberDetail(key: String) {
        val viewList = mutableListOf<ProductEntity>()
        val memberList = mutableListOf<ProductPromotionItems.ProjectMember>()

        viewModelScope.launch {
            val ids = groupRepository.getGroupDetail(key)

            ids.onSuccess { id ->
//                val viewItem = id?.memberIds?.let { member -> ProductEntity("",id.authId, member) }
                val viewItem = id?.memberIds?.let { member -> entity.copy(authId = id.authId, memberIds = member) }
                if (viewItem != null) {
                    viewList.add(viewItem)
                }
                val getLeaderDetail = id?.authId?.let { authId -> userRepository.getUserDetails(authId) }
                val setLeaderItem = ProductPromotionItems.ProjectLeaderItem(getLeaderDetail)

                val mIds = id?.memberIds
                if (mIds != null) {
                    val memberDetailList = mutableListOf<Result<UserEntity?>>()
                    for (member in mIds) {
                        val detail = userRepository.getUserDetails(member)
                        memberDetailList.add(detail)
                    }
                    val setMemberItem = ProductPromotionItems.ProjectMember(memberDetailList)
                    memberList.add(setMemberItem)
                }
                _setLeader.value = setLeaderItem
                _setMember.value = memberList
            }
        }
    }

    fun saveImgUrl(mainUrl : String?, desUrl : String?) : ProductEntity {
        if (mainUrl?.isEmpty() == true) {
            Toast.makeText(context,R.string.product_necessary_img,Toast.LENGTH_SHORT).show()
        }
        return entity.copy(imageUrl = mainUrl, desImg = desUrl)
    }

    fun saveTitleAndDes(title : String?, des : String?) : ProductEntity {
        if (title?.isEmpty() == true || des?.isEmpty() == true) {
            Toast.makeText(context,R.string.product_necessary_title_des,Toast.LENGTH_SHORT).show()
        }
        return entity.copy(title = title, description = des)
    }

    fun saveLink(web : String?, aos : String?, ios : String?) : ProductEntity {
        return entity.copy(referenceUrl = web, aosUrl = aos, iosUrl = ios)
    }

    fun registerProduct() {
        _product.value = entity
        viewModelScope.launch {
            productRepository.registerProduct(entity)
        }
        saveProduct(entity)
    }

    private fun saveProduct(nt : ProductEntity) {
        _product.value = nt
    }
}