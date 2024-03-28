package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.ApplicationInfo
import com.seven.colink.domain.entity.ApplicationInfoEntity
import com.seven.colink.domain.entity.RecruitEntity
import com.seven.colink.domain.repository.RecruitRepository
import com.seven.colink.util.status.ApplicationStatus
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecruitRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): RecruitRepository {
    override suspend fun registerRecruit(recruit: RecruitEntity) =
        suspendCoroutine { continuation ->
            firestore.collection(DataBaseType.RECRUIT.title).document(recruit.key).set(recruit)
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS.apply { message = recruit.key })
                }
                .addOnFailureListener {
                    continuation.resume(DataResultStatus.FAIL.apply {
                        message = it.message ?: "Unknown error"
                    })
                }
        }

    override suspend fun registerApplicationInfo(appInfo: ApplicationInfoEntity) =
        suspendCoroutine { continuation ->
            firestore.collection(DataBaseType.APPINFO.title).document(appInfo.key).set(appInfo)
                .addOnSuccessListener {
                    continuation.resume(DataResultStatus.SUCCESS.apply { message = appInfo.key })
                }
                .addOnFailureListener {
                    continuation.resume(DataResultStatus.FAIL.apply {
                        message = it.message ?: "Unknown error"
                    })
                }
        }

    override suspend fun getRecruit(key: String) = run {
        firestore.collection(DataBaseType.RECRUIT.title).document(key).get().await()
            .toObject(RecruitEntity::class.java)
    }

    override suspend fun getApplicationInfo(key: String) = run {
        firestore.collection(DataBaseType.APPINFO.title).document(key).get().await()
            .toObject(ApplicationInfoEntity::class.java)
    }

    override suspend fun updateStatusApplicationInfo(applicationInfo: ApplicationInfo) =
        suspendCoroutine { continuation ->
            val updateMap = mutableMapOf<String, Any?>()
            updateMap["applicationStatus"] = applicationInfo.applicationStatus

            firestore.collection(DataBaseType.APPINFO.title).document(applicationInfo.key)
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

    override suspend fun getApplicationInfoByUid(uid: String) = runCatching {
        firestore.collection(DataBaseType.APPINFO.title).whereEqualTo("userId", uid)
            .whereEqualTo("applicationStatus","PENDING")
            .get().await()
            .documents.mapNotNull {
                it.toObject(ApplicationInfoEntity::class.java)
            }
    }

    override suspend fun observeRecruitPending() = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid ?: close()
        val listenerRegistration = firestore.collection(DataBaseType.APPINFO.title).whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("observeRecruit", "failed", e)
                    close(e)
                } else if (snapshot != null) {
                    val data = snapshot.toObjects(ApplicationInfoEntity::class.java)
                    trySend(data).isSuccess
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }.mapNotNull { data ->
        data.filter { it.applicationStatus == ApplicationStatus.APPROVE }.takeIf { it.isNotEmpty() }
    }
}