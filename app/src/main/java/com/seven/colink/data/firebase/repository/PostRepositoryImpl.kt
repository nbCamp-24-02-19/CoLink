package com.seven.colink.data.firebase.repository

import com.algolia.search.saas.Index
import com.algolia.search.saas.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.repository.PostRepository
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import com.seven.colink.util.status.ProjectStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
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
            filters.add("groupType:${it}")
        }
        projectStatus?.let {
            filters.add("projectStatus:${it}")
        }
        if (filters.isNotEmpty()) {
            algoliaQuery.setFilters(filters.joinToString(" AND "))
        }
        algolia.searchAsync(algoliaQuery) { jsonArray, exception ->
            if (exception != null) {
                continuation.resumeWithException(exception)
            } else {
                jsonArray?.getJSONArray("hits")?.let { hits ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val results = fetchAllPosts(hits)
                        continuation.resume(results)
                    }
                }
            }
        }
    }

    private suspend fun fetchAllPosts(hits: JSONArray): List<PostEntity> {
        val results = mutableListOf<PostEntity>()
        for (i in 0 until hits.length()) {
            val hit = hits.getJSONObject(i)
            val key = hit.optString("key", "null")
            if (key != "null") {
                fetchPostEntity(key, results)
            }
        }
        return results
    }

    private suspend fun fetchPostEntity(key: String, results: MutableList<PostEntity>) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            val post = getPost(key)
            if (post.isSuccess) {
                synchronized(results) {
                    post.getOrNull()?.let { results.add(it) }
                }
            }
        }
        job.join()
    }
}

