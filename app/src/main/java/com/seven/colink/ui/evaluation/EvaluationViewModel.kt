package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
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
    private val _evalProjectMembersData = MutableLiveData<List<EvaluationData.EvalProject?>?>()
    val evalProjectMembersData : LiveData<List<EvaluationData.EvalProject?>?> get() = _evalProjectMembersData

    private val _evalStudyMembersData = MutableLiveData<List<EvaluationData.EvalStudy?>?>()
    val evalStudyMembersData : LiveData<List<EvaluationData.EvalStudy?>?> get() = _evalStudyMembersData

    init {
        evalProjectMembersData
        evalStudyMembersData
    }


    fun getMembers(groupKey : String?) {

        viewModelScope.launch {
            // 현재 유저
            val currentUser = authRepository.getCurrentUser()
            // 액티비티에서 받은 그룹 키 값으로 GroupEntity의 memberIds를 받아옴
            val getMembers = groupRepository.getGroupDetail(groupKey?:"")
                .getOrNull()?.memberIds?.filter {
                    it != currentUser.message
                }?.map{// map으로 해당 그룹에서 속해 있는 UserEntity 받아옴
                    userRepository.getUserDetails(it).getOrNull()?.convertEvalProjectData()
                }
            _evalProjectMembersData.value = getMembers
            Log.d("Evaluation", "evalMembersData = ${_evalProjectMembersData.value}")
        }
    }

    private fun UserEntity.convertEvalProjectData() =
        EvaluationData.EvalProject(
            uid = uid,
            name = name,
            photoUrl = photoUrl,
            grade = grade,
            communication = communication,
            technic = technicalSkill,
            diligence = diligence,
            flexibility = flexibility,
            creativity = creativity,
            evalCount = evaluatedNumber
        )
}