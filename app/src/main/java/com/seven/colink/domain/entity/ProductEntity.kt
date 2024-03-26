package com.seven.colink.domain.entity

import android.net.Uri
import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

data class ProductEntity (
    val key: String = "PRD_" + UUID.randomUUID().toString(),
    val projectId: String? = "",
    val authId: String? = "",
    val memberIds: List<String>? = emptyList(),
    val title: String? = "",
    val imageUrl: String? = "",
    val description: String? = "",
    val desImg : String? = "",
    val tags: List<String>? = emptyList(),
    val registeredDate: String? = LocalDateTime.now().convertLocalDateTime(),
    val referenceUrl: String? = null,
    val aosUrl: String? = null,
    val iosUrl: String? = null,
)

data class TempProductEntity(
    var mainImg : String? = "",
    var title : String? = "",
    var des : String? = "",
    var desImg : String? = "",
    var web : String? = "",
    var aos : String? = "",
    var ios : String? = "",
    var team : String? = "",
    var selectMainImgUri : Uri? = null,
    var selectMiddleImgUri : Uri? = null
)
