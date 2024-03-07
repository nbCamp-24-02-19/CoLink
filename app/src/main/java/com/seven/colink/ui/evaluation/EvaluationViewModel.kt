package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _evalMembersData = MutableLiveData<List<String>?>()
    val evalMembersData : LiveData<List<String>?> get() = _evalMembersData

    init {
        evalMembersData
    }


    fun getMembers(groupKey : String?) {

        viewModelScope.launch {
            // 현재 유저
            val currentUser = authRepository.getCurrentUser()
            // 액티비티에서 받은 그룹 키 값으로 GroupEntity의 memberIds를 받아옴
            val getMembers = groupRepository.getGroupDetail(groupKey?:"")
                .getOrNull()?.memberIds?.filter {
                    it != currentUser.message
                }
            _evalMembersData.value = getMembers
            Log.d("Evaluation", "evalMembersData = ${_evalMembersData.value}")
        }
    }
}