package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GroupRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): GroupRepository {
    override suspend fun registerGroup(group: GroupEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.GROUP.title).document(group.key).set(group)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply { message = e.message?: "Unknown error" })
            }
    }

    override suspend fun getGroupDetail(key: String) = runCatching {
        firestore.collection(DataBaseType.GROUP.title).document(key).get().await()
            .toObject(GroupEntity::class.java)
    }
}