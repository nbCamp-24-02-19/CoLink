package com.seven.colink.domain.usecase

import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.ApplicationInfoEntity
import com.seven.colink.domain.repository.RecruitRepository
import javax.inject.Inject

class RegisterApplicationInfoUseCase @Inject constructor(
    private val recruitRepository: RecruitRepository,
) {
    suspend operator fun invoke(
        applicationInfo: ApplicationInfo,
    ) {
        applicationInfo.recruitId?.let { key ->
            recruitRepository.getRecruit(key)
                .let { recruit ->
                    recruit?.copy(
                        applicationInfo = recruit.applicationInfo?.let {
                            if (it.contains(applicationInfo.key)) it else it + applicationInfo.key
                        } ?: listOf(applicationInfo.key))
                }?.let {
                    recruitRepository.registerRecruit(
                        it
                    )
                }
        }
        recruitRepository.registerApplicationInfo(applicationInfo.convert())

    }

    private fun ApplicationInfo.convert() = ApplicationInfoEntity(
        key, recruitId, userId, applicationStatus, applicationDate
    )
}