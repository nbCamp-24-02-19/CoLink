package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.group.GroupData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationProjectViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _evalProjectData = MutableLiveData<List<EvaluationData.EvalProject?>?>()
    val evalProjectData : LiveData<List<EvaluationData.EvalProject?>?> get() = _evalProjectData

    private val _evalUserData = MutableLiveData<List<EvaluationData>?>()
    val evalUserData : LiveData<List<EvaluationData>?> get() = _evalUserData

    init {
        viewModelScope.launch {
            getMembers()
            itemUpdate()
        }
    }

    private fun itemUpdate() {
        viewModelScope.launch {
            val items = mutableListOf<EvaluationData.EvalProject?>()
            evalProjectData.map { userData -> userData?.map { items.add(it) } }
        }
    }

    private suspend fun getMembers() {
        // 현재 유저
        val currentUser = authRepository.getCurrentUser()
        // 현재 유저 키를 통해서 그룹 속성 가져오고 memberIds 받아옴
        val getUserEntity = groupRepository.getGroupDetail(currentUser.message)
            .getOrNull()?.memberIds?.filter { // filter로 본인 제외
            it != currentUser.message
        }?.map { // map으로 해당 그룹에서 속해 있는 UserENtity 받아옴
            userRepository.getUserDetails(it).getOrNull()?.convertEvalProjectData()
        }

        // evalProjectData에 그룹에 속해있는 UserEntity 리스트로 넣어줌
        _evalProjectData.value = getUserEntity
        Log.d("Evaluation","getGroupEntity = ${getUserEntity}")
    }

//    private fun GroupEntity.convertUserEntity() =
//        UserEntity(
//            uid = key,
//            name = ,
//            photoUrl = ,
//            grade = ,
//            communication = ,
//            technicalSkill = ,
//            diligence = ,
//            flexibility = ,
//            creativity = ,
//            evaluatedNumber =
//        )
//
    // getMembers에서 받은 UserEntity를 EvaluationData.EvalProject으로 컨버트 해줌
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