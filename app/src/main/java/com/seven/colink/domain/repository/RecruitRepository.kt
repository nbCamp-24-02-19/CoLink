package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.ApplicationInfoEntity
import com.seven.colink.domain.entity.RecruitEntity
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.flow.Flow

interface RecruitRepository {

    suspend fun registerRecruit(recruit: RecruitEntity): DataResultStatus
    suspend fun registerApplicationInfo(appInfo: ApplicationInfoEntity): DataResultStatus
    suspend fun getRecruit(key: String): RecruitEntity?
    suspend fun getApplicationInfo(key: String): ApplicationInfoEntity?
    suspend fun updateStatusApplicationInfo(applicationInfo: ApplicationInfo): DataResultStatus
    suspend fun getApplicationInfoByUid(uid: String): Result<List<ApplicationInfoEntity>>
    suspend fun observeRecruitPending(): Flow<List<ApplicationInfoEntity>>
}
