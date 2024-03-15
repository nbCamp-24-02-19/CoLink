package com.seven.colink.ui.sign.signup.model

sealed interface SignUpProfileItem {
    data class Category(
        val mainSpecialty: String? = null,
        val specialty: String? = null,
    ): SignUpProfileItem

    data class Skill(
        val skill: List<String>? = null
    ): SignUpProfileItem

    data class Level(
        val level: Int? = 1
    ): SignUpProfileItem

    data class Info(
        val info: String? = null
    ): SignUpProfileItem

    data class Blog(
        val git: String? = null,
        val blog: String? = null,
        val link: String? = null,
    ): SignUpProfileItem
}
