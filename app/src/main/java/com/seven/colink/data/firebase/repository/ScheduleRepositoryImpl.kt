package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.ScheduleEntity
import com.seven.colink.domain.repository.ScheduleRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ScheduleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ScheduleRepository {
    override suspend fun registerSchedule(schedule: ScheduleEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.SCHEDULE.title).document(schedule.key).set(schedule)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener {
                continuation.resume(DataResultStatus.FAIL.apply { message = it.message?: "Unknown Error" })
            }
    }

    override suspend fun getScheduleByPostId(postId: String) = run {
        firestore.collection(DataBaseType.SCHEDULE.title).whereEqualTo("postId", postId).get()
            .await()
            .documents.mapNotNull {
                it.toObject(ScheduleEntity::class.java)
            }
    }
}