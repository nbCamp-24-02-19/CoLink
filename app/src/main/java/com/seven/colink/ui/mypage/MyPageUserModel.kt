package com.seven.colink.ui.mypage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyPageUserModel(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val mainSpecialty: String? = null,
    val specialty: String? = null,
    val skill: List<String>? = emptyList(),
    val level: Int? = null,
    val info: String? = null,
    val git: String? = null,
    val blog: String? = null,
    val link: String? = null
): Parcelable
