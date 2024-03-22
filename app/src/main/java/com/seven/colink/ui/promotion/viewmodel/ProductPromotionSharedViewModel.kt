package com.seven.colink.ui.promotion.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seven.colink.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductPromotionSharedViewModel @Inject constructor(
    private val productRepository : ProductRepository
) : ViewModel() {
    private val _key = MutableLiveData<String>()
    val key : LiveData<String> get() = _key


}