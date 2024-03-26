package com.seven.colink.ui.promotion.model

import com.seven.colink.domain.entity.UserEntity

data class ProductItems(
    val key : String,
    val userId : String,
    val authId : List<MembersItem>,
    val img : String,
    val title : String,
    val info : String,
    val date : String,
    val link : List<LinkItem>,
    val des : String,
    val memberId : List<MembersItem>
)

data class LinkItem(
    val link : String?
)

data class ViewLinkImg(
    val web : String?,
    val aos : String?,
    val ios : String?
)

data class MembersItem(
    val uid : String?,
    val name : String?,
    val img : String?,
    val level : Int?,
    val grade : Double?,
    val info : String?
)

sealed class ProductPromotionItems {
    data class Img(
        val img : String?
    ) : ProductPromotionItems()

    data class Title(
        val title : String?,
        val date : String?,
        val des : String?,
        val team : String?,
        val web : String?,
        val aos : String?,
        val ios : String?
    ) : ProductPromotionItems()

    data class MiddleImg(
        val img : String?
    ) : ProductPromotionItems()

    data class Link(
        val webLink : String?,
        val iosLink : String?,
        val aosLink : String?
    ) : ProductPromotionItems()

    data class ProjectHeader(
        val header: String
    ) : ProductPromotionItems()

    data class ProjectLeaderHeader(
        val header: String
    ) : ProductPromotionItems()

    data class ProjectLeaderItem(
        val userInfo: Result<UserEntity?>?
    ) : ProductPromotionItems()


    data class ProjectMemberHeader(
        val header: String
    ) : ProductPromotionItems()

    data class ProjectMember(
        val userInfo: UserEntity?
    ) : ProductPromotionItems()
}