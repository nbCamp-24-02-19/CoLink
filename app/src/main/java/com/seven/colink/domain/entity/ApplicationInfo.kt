package com.seven.colink.domain.entity

import android.os.Parcelable
import com.seven.colink.util.convert.convertLocalDateTime
import com.seven.colink.util.status.ApplicationStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class ApplicationInfo(
    val userId: String? = "", // 지원한 사용자 아이디
    val applicationStatus: ApplicationStatus? = ApplicationStatus.PENDING, // 지원 상태
    val applicationDate: String? = LocalDateTime.now().convertLocalDateTime(), // 지원한 날짜
) : Parcelable
