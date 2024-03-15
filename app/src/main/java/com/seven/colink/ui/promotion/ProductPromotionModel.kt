package com.seven.colink.ui.promotion

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
        val key : String?,
        val img : String?
    ) : ProductPromotionItems()

    data class Title(
        val key : String?,
        val title : String?,
        val date : String?,
        val des : String?
    ) : ProductPromotionItems()

    data class MiddleImg(
        val key : String?,
        val img : String?
    ) : ProductPromotionItems()

    data class Link(
        val key : String?,
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

    data class ProjectLeaderItem(     // 로그인 되어있는, 작성하는 사람 정보 가져오기
//        val key : String?,
        val userInfo: Result<UserEntity?>?
//        val userInfo: UserEntity
    ) : ProductPromotionItems()

//    data class ProjectMemberItem(
//        val uid : String?,
//        val name : String?,
//        val img : String?,
//        val level : Int?,
//        val grade : Double?,
//        val info : String?
//    ) : ProductPromotionItems()

    data class ProjectMemberHeader(
        val header: String
    ) : ProductPromotionItems()

//    data class ProjectMember(
//        val uid : String?,
//        val name : String?,
//        val img : String?,
//        val level : Int?,
//        val grade : Double?,
//        val info : String?
//    ) : ProductPromotionItems()
    data class ProjectMember(      // 프로젝트의 멤버를 가져와서 작성한 본인은 빼기
//        val key : String?,
//    val userInfo: Result<UserEntity?>
    val userInfo: MutableList<Result<UserEntity?>>
    ) : ProductPromotionItems()

}