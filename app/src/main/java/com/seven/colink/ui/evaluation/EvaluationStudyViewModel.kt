package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationStudyViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel(){

    private val _evalStudyData = MutableLiveData<List<EvaluationData.EvalStudy?>?>()
    val evalStudyData : LiveData<List<EvaluationData.EvalStudy?>?> get() = _evalStudyData

    private val _evalUserData = MutableLiveData<List<EvaluationData>?>()
    val evalUserData: LiveData<List<EvaluationData>?> get() = _evalUserData

    init {
        viewModelScope.launch {
            getMembers()
            itemUpdate()
        }
    }

    private fun itemUpdate() {
        viewModelScope.launch {
            val items = mutableListOf<EvaluationData.EvalStudy?>()
            evalStudyData.map { userData-> userData?.map { items.add(it) } }
        }
    }

    private suspend fun getMembers(){
        val currentUser = authRepository.getCurrentUser()
        val getUserEntity = groupRepository.getGroupDetail(currentUser.message)
            .getOrNull()?.memberIds?.filter {
                it != currentUser.message
            }?.map {
                userRepository.getUserDetails(it).getOrNull()?.convertEvalStudyData()
            }

        _evalStudyData.value = getUserEntity
        Log.d("Evaluation", "getGroupEntity = $getUserEntity")
    }

    private fun UserEntity.convertEvalStudyData() =
        EvaluationData.EvalStudy(
            uid = uid,
            name = name,
            photoUrl = photoUrl,
            grade = grade,
            diligence = diligence,
            communication = communication,
            flexibility = flexibility,
            evalCount = evaluatedNumber
        )
}