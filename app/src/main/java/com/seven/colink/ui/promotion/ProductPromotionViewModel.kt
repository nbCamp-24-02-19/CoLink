package com.seven.colink.ui.promotion

import android.app.Application
import androidx.lifecycle.ViewModel
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.ImageRepository
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductPromotionEditViewModel @Inject constructor(
    private val context: Application,
    private val postRepository: PostRepository,
    private val groupRepository: GroupRepository,
    private val productRepository : ProductRepository,
    private val imageRepository: ImageRepository,

) : ViewModel() {

}