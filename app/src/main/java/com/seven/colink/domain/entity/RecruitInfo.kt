package com.seven.colink.domain.entity

import android.os.Parcelable
import com.seven.colink.util.status.ApplicationStatus
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class RecruitInfo(
    val key: String? = null,
    val type: String? = "", // 모집 분야
    val maxPersonnel: Int? = -1, // 모집 최대 인원 수
    val applicationInfos: List<ApplicationInfo>? = emptyList()
) : Parcelable {
    val nowPersonnel: Int
        get() = applicationInfos?.count { it.applicationStatus == ApplicationStatus.APPROVE } ?: 0
}