package com.seven.colink.ui.evaluation

sealed interface EvaluationData {
    data class EvalProject(
        val key : String? = "",
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        val grade : Double? = 5.0,
        val communication: Int? = null,
        val technic: Int? = null,
        val diligence: Int? = null,
        val flexibility: Int? = null,
        val creativity: Int? = null,
        val evalCount: Int = 0
    ) : EvaluationData
    data class EvalStudy(
        val key : String? = "",
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        val grade : Double? = 5.0,
        val diligence: Int? = null,
        val communication: Int? = null,
        val flexibility: Int? = null,
        val evalCount: Int = 0
    ) : EvaluationData
}