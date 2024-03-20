package com.seven.colink.ui.evaluation

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.seven.colink.domain.entity.UserEntity
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch

@Parcelize
sealed interface EvaluationData : Parcelable {
    @Parcelize
    data class EvalProject(
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        var grade : Double? = 5.0,
        var communication: Float?,
        var technic: Float?,
        var diligence: Float?,
        var flexibility: Float?,
        var creativity: Float?,
        var evalCount: Int = 0
    ) : EvaluationData
    @Parcelize
    data class EvalStudy(
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        var grade : Double? = 5.0,
        var diligence: Float?,
        var communication: Float?,
        var flexibility: Float?,
        var evalCount: Int = 0
    ) : EvaluationData
}