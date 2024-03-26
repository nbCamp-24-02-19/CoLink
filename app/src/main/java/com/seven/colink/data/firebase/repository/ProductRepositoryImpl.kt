package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.PostEntity
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.domain.repository.ProductRepository
import com.seven.colink.util.status.DataResultStatus
import com.seven.colink.util.status.GroupType
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ProductRepository {
    override suspend fun registerProduct(product: ProductEntity) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.PRODUCT.title).document(product.key).set(product)
            .addOnSuccessListener {
                continuation.resume(DataResultStatus.SUCCESS)
            }
            .addOnFailureListener { e ->
                continuation.resume(DataResultStatus.FAIL.apply { message = e.message?: "Unknown error" })
            }
    }

    override suspend fun getProductDetail(key: String) = runCatching {
        firestore.collection(DataBaseType.PRODUCT.title).document(key).get().await()
            .toObject(ProductEntity::class.java)
    }

    override suspend fun getProductsByAuthId(authId: String) = runCatching {
        firestore.collection(DataBaseType.PRODUCT.title).whereEqualTo("authId", authId).get().await()
            .documents.mapNotNull {
                it.toObject(ProductEntity::class.java)
            }
    }

    override suspend fun getProductsByContainUserId(userId: String) = runCatching {
        firestore.collection(DataBaseType.PRODUCT.title).whereArrayContains("memberIds", userId).get().await()
            .documents.mapNotNull {
                it.toObject(ProductEntity::class.java)
            }
    }

    override suspend fun getRecentPost(count: Int): List<ProductEntity> {
        var query = firestore.collection(DataBaseType.PRODUCT.title)
            .orderBy("registeredDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(count.toLong())

        return query.get().await().documents.mapNotNull { snapshot ->
            snapshot.toObject(ProductEntity::class.java)
        }
    }

    override suspend fun deleteProduct(key: String) = suspendCoroutine { continuation ->
        firestore.collection(DataBaseType.PRODUCT.title).document(key).delete()
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