package com.seven.colink.ui.post.register.recommend.type

import com.seven.colink.util.model.MemberCard
import com.seven.colink.util.model.MemberInfo

sealed interface RecommendType {
    data class Title (val name: String, val key: String): RecommendType
    data class Card(val memberCard: MemberCard?): RecommendType
    data class Middle (val memberName: String): RecommendType
    data class Others (val memberInfo: MemberInfo?): RecommendType
    data object Close: RecommendType
}
