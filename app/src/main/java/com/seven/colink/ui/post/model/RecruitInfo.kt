package com.seven.colink.ui.post.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecruitInfo(
    val type: String = "",
    val level: Int = -1,
    val maxPersonnel: Int = -1,
    val nowPersonnel: Int = -1
) : Parcelable