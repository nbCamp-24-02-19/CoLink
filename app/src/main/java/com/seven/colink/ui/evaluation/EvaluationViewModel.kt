package com.seven.colink.ui.evaluation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.UserEntity
import com.seven.colink.domain.repository.AuthRepository
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.domain.repository.UserRepository
import com.seven.colink.ui.evaluation.EvaluationActivity.Companion.EXTRA_GROUP_ENTITY
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.PageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val handle: SavedStateHandle,
) : ViewModel() {
    private val _evalProjectMembersData = MutableLiveData<List<EvaluationData.EvalProject?>?>()
    val evalProjectMembersData: LiveData<List<EvaluationData.EvalProject?>?> get() = _evalProjectMembersData

    private val _evalStudyMembersData = MutableLiveData<List<EvaluationData.EvalStudy?>?>()
    val evalStudyMembersData: LiveData<List<EvaluationData.EvalStudy?>?> get() = _evalStudyMembersData

    private val _currentPage = MutableStateFlow(PageState.FIRST)
    val currentPage = _currentPage.asStateFlow()

    private val _currentGroup = MutableStateFlow(GroupEntity())
    val currentGroup = _currentGroup.asStateFlow()

    private val _currentUid = MutableStateFlow("")
    val currentUid = _currentUid.asStateFlow()

    private val _result = MutableSharedFlow<DataResultStatus>()
    val result = _result.asSharedFlow()
    private val groupKey get() = handle.get<String>(EXTRA_GROUP_ENTITY)

    init {
        viewModelScope.launch {
            _currentGroup.value =
                groupRepository.getGroupDetail(groupKey ?: return@launch).getOrNull()
                    ?: return@launch
        }

        viewModelScope.launch {
            _currentUid.value = authRepository.getCurrentUser().message
        }
    }


    fun getProjectMembers(groupEntity: GroupEntity, uid: String) {
        viewModelScope.launch {
            // 현재 유저
            // 액티비티에서 받은 그룹 키 값으로 GroupEntity의 memberIds를 받아옴
            _evalProjectMembersData.value = groupEntity.memberIds.filter {
                it != uid
            }.mapNotNull {// map으로 해당 그룹에서 속해 있는 UserEntity 받아옴
                userRepository.getUserDetails(it).getOrNull()?.convertEvalProjectData()
            }
        }
    }

    fun updateProjectMembers(
        position: Int,
        q1: Float? = null,
        q2: Float? = null,
        q3: Float? = null,
        q4: Float? = null,
        q5: Float? = null
    ) {
        val projectMembers = _evalProjectMembersData.value?.toMutableList() ?: return

        if (position >= 0 && position < projectMembers.size) {
            val member = projectMembers[position]
            member?.let {
                it.communication = q1?: 2.5F
                it.technic = q2?: 2.5F
                it.diligence = q3?: 2.5F
                it.flexibility = q4?: 2.5F
                it.creativity = q5?: 2.5F
                it.grade = ((it.communication!! + it.technic!! + it.diligence!! + it.flexibility!! + it.creativity!!) / 5).toDouble()
            }
            _evalProjectMembersData.value = projectMembers
        }
    }

    // 완료 버튼 클릭 시 user의 grade를 계산 후, 저장 시켜주기
    fun updateProjectUserGrade(groupEntity: GroupEntity, currentUid: String) {
        viewModelScope.launch {
            try {
                evalProjectMembersData.value?.map { data ->
                    async {
                        userRepository.getUserDetails(data?.uid!!).getOrNull().let { member ->
                                userRepository.updateUserInfo(
                                member!!.copy(
                                    grade = ((member.grade!! * member.evaluatedNumber) + (data.grade!! * 2)) / ++data.evalCount,
                                    communication = data.communication,
                                    technicalSkill = data.technic,
                                    diligence = data.diligence,
                                    flexibility = data.flexibility,
                                    creativity = data.creativity,
                                    evaluatedNumber = ++data.evalCount,
                                )
                            )
                        }
                    }
                }?.awaitAll()
                    _result.emit(groupRepository.registerGroup(
                        groupEntity.let {
                            it.copy(evaluateMember = it.evaluateMember?.plus(currentUid)?: listOf(currentUid))
                        }))
            } catch (e: Exception){
                _result.emit(DataResultStatus.FAIL.apply { message = e.message?: "알수 없는 에러" })
            }
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

    fun getStudyMembers(groupEntity: GroupEntity, uid: String) {
        viewModelScope.launch {
            _evalStudyMembersData.value = groupEntity.memberIds.filter {
                it != uid
            }.mapNotNull {
                userRepository.getUserDetails(it).getOrNull()?.convertEvalStudyData()
            }
        }
    }

    fun updateStudyMembers(
        position: Int,
        q1: Float? = null,
        q2: Float? = null,
        q3: Float? = null
    ) {
        val studyMembers = _evalStudyMembersData.value?.toMutableList() ?: return

        if (position >= 0 && position < studyMembers.size) {
            val member = studyMembers[position]
            member?.let {
                it.diligence = q1?: 2.5f
                it.communication = q2?: 2.5f
                it.flexibility = q3?: 2.5f
                it.grade = ((it.diligence!! + it.communication!! + it.flexibility!!) / 3).toDouble()
            }
            _evalStudyMembersData.value = studyMembers
        }
    }

    fun updateStudyUserGrade(groupEntity: GroupEntity, currentUid: String) {
        viewModelScope.launch {
            try {
                evalStudyMembersData.value?.map { data ->
                    async {
                        userRepository.getUserDetails(data?.uid!!).getOrNull().let { member ->
                            userRepository.updateUserInfo(
                                member!!.copy(
                                    grade = ((member.grade!! * member.evaluatedNumber) + (data.grade!! * 2)) / ++data.evalCount,
                                    diligence = data.diligence,
                                    communication = data.communication,
                                    flexibility = data.flexibility,
                                    evaluatedNumber = ++data.evalCount
                                )
                            )
                        }
                    }
                }?.awaitAll()
                _result.emit(groupRepository.registerGroup(
                    groupEntity.let {
                        it.copy(
                            evaluateMember = it.evaluateMember?.plus(currentUid) ?: listOf(
                                currentUid
                            )
                        )
                    }
                ))
            }catch (e: Exception){
                _result.emit(DataResultStatus.FAIL.apply { message = e.message?: "알 수 없는 에러" })
            }
        }
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

    fun updatePage(position: Int) {
        Log.d("qwew", "postion $position \n size ${currentGroup.value.memberIds}")
            _currentPage.value =
                when (position) {
                    currentGroup.value.memberIds.size - 2 -> PageState.LAST
                    0 -> if (currentGroup.value.memberIds.size != 2) PageState.FIRST else PageState.LAST
                    else -> PageState.MIDDLE
                }.apply { num = position }
        }
}