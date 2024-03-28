package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.repository.GroupRepository
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.ProjectStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GroupRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : GroupRepository {
    override suspend fun registerGroup(group: GroupEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.GROUP.title).document(group.key).set(group)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply {
                    message = e.message ?: "Unknown error"
                })
            }
    }

    override suspend fun getGroupDetail(key: String) = runCatching {
        firestore.collection(DataBaseType.GROUP.title).document(key).get().await()
            .toObject(GroupEntity::class.java)
    }

    override suspend fun updateGroupSection(key: String, updatedGroup: GroupEntity) =
        suspendCoroutine { continuation ->
            val updateMap = mutableMapOf<String, Any?>().apply {
                updatedGroup.teamName?.let { put("teamName", it) }
                updatedGroup.description?.let { put("description", it) }
                updatedGroup.tags?.let { put("tags", it) }
                updatedGroup.imageUrl?.let { put("imageUrl", it) }
                updatedGroup.precautions?.let { put("precautions", it) }
                updatedGroup.startDate?.let { put("startDate", it) }
                updatedGroup.endDate?.let { put("endDate", it) }

            }

            firestore.collection(DataBaseType.GROUP.title).document(key)
                .update(updateMap)
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    continuation.resume(DataResultStatus.FAIL.apply {
                        message = e.message ?: "Unknown error"
                    })
                }
        }

    override suspend fun updateGroup(key: String, updatedGroup: GroupEntity) =
        suspendCoroutine { continuation ->
            firestore.collection(DataBaseType.GROUP.title).document(key)
                .set(updatedGroup, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    continuation.resume(DataResultStatus.FAIL.apply {
                        message = e.message ?: "Unknown error"
                    })
                }
        }

    override suspend fun getGroupByContainUserId(userId: String) = runCatching {
        firestore.collection(DataBaseType.GROUP.title)
            .whereArrayContains("memberIds", userId)
            .get().await()
            .documents.mapNotNull {
                it.toObject(GroupEntity::class.java)
            }
    }

    private suspend fun updateFirestoreField(key: String, fieldMap: Map<String, Any?>): DataResultStatus =
        suspendCoroutine { continuation ->
            firestore.collection(DataBaseType.GROUP.title).document(key)
                .update(fieldMap)
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    continuation.resume(DataResultStatus.FAIL.apply {
                        message = e.message ?: "Unknown error"
                    })
                }
        }

    override suspend fun updateGroupStatus(key: String, status: ProjectStatus, date: Map<String, String>): DataResultStatus {
        val fieldMap = mutableMapOf<String, Any>("status" to status)
        fieldMap.putAll(date)
        return updateFirestoreField(key, fieldMap)
    }

    override suspend fun updateGroupMemberIds(key: String, updatedGroup: GroupEntity): DataResultStatus {
        val fieldMap = mapOf("memberIds" to updatedGroup.memberIds)
        return updateFirestoreField(key, fieldMap)
    }

    override suspend fun observeGroupState() = callbackFlow {
        val uid = auth.currentUser?.uid ?: close()
        val listener = firestore.collection(DataBaseType.GROUP.title).whereArrayContains("memberIds", uid)
        .addSnapshotListener { snapshot, e ->
                    if(e != null) {
                        Log.w("observeGroupState", "failed", e)
                    } else if (snapshot != null && !snapshot.isEmpty) {
                        trySend(
                            snapshot.toObjects(GroupEntity::class.java)
                        ).isSuccess
                    }
        }
        awaitClose {
            listener.remove()
        }
    }.map { data ->
        data.filter {
            it.status != ProjectStatus.RECRUIT && it.evaluateMember?.contains(auth.currentUser?.uid)?.not()?: true && it.memberIds.size != 1
        }.takeIf {
            it.isNotEmpty()
        }
    }
}