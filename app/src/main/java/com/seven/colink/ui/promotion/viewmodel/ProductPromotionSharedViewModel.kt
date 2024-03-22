package com.seven.colink.ui.promotion.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProductPromotionSharedViewModel @Inject constructor(
    handle : SavedStateHandle
) : ViewModel() {
    private val _key = MutableStateFlow(handle.get<String>(Constants.EXTRA_ENTITY_KEY))
    val key: StateFlow<String?> = _key

    fun setKey(newKey: String) {
        _key.value = newKey
    }
}