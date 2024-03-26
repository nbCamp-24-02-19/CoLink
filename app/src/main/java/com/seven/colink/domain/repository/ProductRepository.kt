package com.seven.colink.domain.repository

import com.seven.colink.domain.entity.GroupEntity
import com.seven.colink.domain.entity.ProductEntity
import com.seven.colink.util.status.DataResultStatus

interface ProductRepository {

    suspend fun registerProduct(product: ProductEntity): DataResultStatus
//    suspend fun getProductDetail(key: String): Result<GroupEntity?>
    suspend fun getProductsByAuthId(authId: String): Result<List<ProductEntity>>
    suspend fun getProductsByContainUserId(userId: String): Result<List<ProductEntity>>
    suspend fun deleteProduct(key: String): DataResultStatus
    suspend fun getProductDetail(key: String): Result<ProductEntity?>
    suspend fun getRecentPost(count: Int) : List<ProductEntity>

}
