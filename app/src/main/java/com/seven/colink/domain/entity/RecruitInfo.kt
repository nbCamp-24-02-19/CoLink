package com.seven.colink.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class RecruitInfo(
    val key: String = UUID.randomUUID().toString(),
    val type: String = "",
    val level: Int = -1,
    val maxPersonnel: Int = -1,
    val nowPersonnel: Int = -1
) : Parcelable