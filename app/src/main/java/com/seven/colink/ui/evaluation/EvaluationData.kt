package com.seven.colink.ui.evaluation

sealed interface EvaluationData {
    data class EvalProject(
        val key : String? = "",
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        val grade : Double? = 5.0,
        val communication: Int,
        val technic: Int,
        val diligence: Int,
        val flexibility: Int,
        val creativity: Int,
        val evalCount: Int = 0
    ) : EvaluationData
    data class EvalStudy(
        val key : String? = "",
        val uid : String? = "",
        val name: String? = "",
        val photoUrl: String? = "",
        val grade : Double? = 5.0,
        val diligence: Int,
        val communication: Int,
        val flexibility: Int,
        val evalCount: Int = 0
    ) : EvaluationData
}