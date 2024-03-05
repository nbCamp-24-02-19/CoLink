package com.seven.colink.ui.evaluation

sealed class EvaluationData {
    data class EvalProject(var name: String) : EvaluationData()
    data class EvalStudy(var name: String) : EvaluationData()
}