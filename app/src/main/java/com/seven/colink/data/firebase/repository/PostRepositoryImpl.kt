package com.seven.colink.data.firebase.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PostRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val algolia: Index,
) : PostRepository {
    override suspend fun registerPost(post: PostEntity) = suspendCoroutine { continuation ->
        firebaseFirestore.collection(DataBaseType.POST.title).document(post.key).set(post)
            .addOnSuccessListener {
                addPostToAlgolia(post)
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply {
                    message = e.message ?: "Unknown error"
                })
            }
    }

    override suspend fun getPostByAuthId(authId: String) = runCatching {
        firebaseFirestore.collection(DataBaseType.POST.title).whereEqualTo("authId", authId).get()
            .await()
            .documents.mapNotNull {
                it.toObject(PostEntity::class.java)
            }
    }

    override suspend fun getPostByContainUserId(userId: String) = runCatching {
        firebaseFirestore.collection(DataBaseType.POST.title)
            .whereArrayContains("memberIds", userId)
            .get().await()
            .documents.mapNotNull {
                it.toObject(PostEntity::class.java)
            }
    }

    override suspend fun deletePost(key: String) = suspendCoroutine { continuation ->
        firebaseFirestore.collection(DataBaseType.POST.title).document(key).delete()
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply {
                    message = e.message ?: "Unknown error"
                })
            }
    }

    override suspend fun getPost(key: String) = runCatching {
        firebaseFirestore.collection(DataBaseType.POST.title).document(key)
            .get().await()
            .toObject(PostEntity::class.java)
    }

    override suspend fun searchQuery(query: String, groupType: GroupType?, projectStatus: ProjectStatus?) = suspendCancellableCoroutine { continuation ->
        val algoliaQuery = Query(query)
        val filters = mutableListOf<String>()
        groupType?.let {
            filters.add("groupType:\"${it}\"")
        }
        projectStatus?.let {
            filters.add("status:\"${it}\"")
        }
        if (filters.isNotEmpty()) {
            algoliaQuery.filters = filters.joinToString(" AND ")
        }
        algolia.searchAsync(algoliaQuery) { jsonArray, exception ->
            if (exception != null) {
                continuation.resumeWithException(exception)
            } else {
                jsonArray?.getJSONArray("hits")?.let { hits ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val posts = fetchAllPosts(hits)
                            continuation.resume(posts)
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    }
                } ?: continuation.resumeWithException(RuntimeException("No hits found"))
            }
        }
    }

    private suspend fun fetchAllPosts(hits: JSONArray): List<PostEntity> = withContext(Dispatchers.IO) {
        val hitSequence = (0 until hits.length()).asSequence().map { hits.getJSONObject(it) }
        hitSequence.mapNotNull { hit ->
            hit.optString("objectID", null)?.let { key ->
                Log.d("SearchDebug", "Processing post with key: $key")
                async { fetchPostEntity(key) }
            }
        }.toList().awaitAll().filterNotNull()
    }

    private suspend fun fetchPostEntity(key: String): PostEntity? = getPost(key).getOrNull()

    override suspend fun getRecentPost(count: Int, groupType: GroupType?): List<PostEntity> {
        var query = firebaseFirestore.collection(DataBaseType.POST.title)
            .orderBy("registeredDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(count.toLong())

        if (groupType != null) {
            query = query.whereEqualTo("groupType", groupType.name)
        }

        return query.get().await().documents.mapNotNull { snapshot ->
            snapshot.toObject(PostEntity::class.java)
        }
    }

    override suspend fun updatePost(key: String, updatedPost: PostEntity) =
        suspendCoroutine { continuation ->
            firebaseFirestore.collection(DataBaseType.POST.title).document(key)
                .set(updatedPost, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    addPostToAlgolia(updatedPost)
                    continuation.resume(DataResultStatus.SUCCESS)
                }
                .addOnFailureListener { e ->
                    continuation.resume(DataResultStatus.FAIL.apply {
                        message = e.message ?: "Unknown error"
                    })
                }
        }

    override suspend fun incrementPostViews(key: String): DataResultStatus {
        return try {
            firebaseFirestore.collection(DataBaseType.POST.title).document(key)
                .update("views", FieldValue.increment(1))
                .await()
            DataResultStatus.SUCCESS
        } catch (e: Exception) {
            DataResultStatus.FAIL.apply {
                message = e.message ?: "Failed to increment views"
            }
        }
    }

    private fun addPostToAlgolia(post: PostEntity) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val postJson = JSONObject().apply {
                put("objectID", post.key)
                put("description", post.description)
                put("title", post.title)
                put("tags", JSONArray(post.tags))
                put("groupType", post.groupType)
                put("status", post.status)
                put("registeredDate", post.registeredDate)
            }
            algolia.addObjectAsync(postJson) { _, exception ->
                if (exception != null) {
                }
            }
        } catch (e: Exception){
            Log.e(TAG, "$e")
        }
    }

    override suspend fun synchronizeFirestoreToAlgolia() = withContext(Dispatchers.IO) {
        firebaseFirestore.collection(DataBaseType.POST.title).get().await()
            .documents.forEach { documentSnapshot ->
            documentSnapshot.toObject(PostEntity::class.java)?.let { postEntity ->
                addPostToAlgolia(postEntity)
            }
        }
    }

    override suspend fun registerLike(key: String, likes:Int) = suspendCoroutine { continuation ->
        firebaseFirestore.collection(DataBaseType.POST.title).document(key).update("like", likes)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply {
                    message = e.message ?: "Unknown error"
                })
            }
    }
}