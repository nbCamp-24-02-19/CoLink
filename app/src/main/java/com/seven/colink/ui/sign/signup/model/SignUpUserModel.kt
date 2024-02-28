package com.seven.colink.ui.sign.signup.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpUserModel(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val specialty: String? = null,
    val skill: List<String>? = emptyList(),
    val level: Int? = null,
    val info: String? = null,
    val git: String? = null,
    val blog: String? = null,
    val link: String? = null
): Parcelable
