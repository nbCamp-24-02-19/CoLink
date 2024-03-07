package com.seven.colink.ui.evaluation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
sealed interface EvaluationData : Parcelable {
    @Parcelize
    data class EvalProject(
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        val grade : Double? = 5.0,
        val communication: Float?,
        val technic: Float?,
        val diligence: Float?,
        val flexibility: Float?,
        val creativity: Float?,
        val evalCount: Int = 0
    ) : EvaluationData
    @Parcelize
    data class EvalStudy(
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        val grade : Double? = 5.0,
        val diligence: Float?,
        val communication: Float?,
        val flexibility: Float?,
        val evalCount: Int = 0
    ) : EvaluationData
}