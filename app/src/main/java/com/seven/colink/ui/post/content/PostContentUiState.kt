package com.seven.colink.ui.post.content

data class PostContentUiState(
    val list: List<PostContentItem>
) {
    companion object {
        fun init() = PostContentUiState(
            list = emptyList()
        )
    }
}

//data class PostUiState(
//    val title: String?,
//    val imageUrl: String?,
//    val status: ProjectStatus?,
//    val groupType: GroupType?,
//    val description: String?,
//    val tags: List<String>?,
//    val precautions: String?,
//    val recruitInfo: String?,
//    val recruit: RecruitInfo?,
//    val registeredDate: String?,
//    val editDate: String?,
//    val views: Int?,
//    val startDate: String?,
//    val endDate: String?,
//    val memberIds: List<String>?,
//) {
//    companion object {
//        fun init() = PostUiState(
//            title = null,
//            imageUrl = null,
//            status = null,
//            groupType = null,
//            description = null,
//            tags = null,
//            precautions = null,
//            recruitInfo = null,
//            recruit = null,
//            registeredDate = null,
//            editDate = null,
//            views = null,
//            startDate = null,
//            endDate = null,
//            memberIds = null,
//        )
//    }
//}

sealed interface PostContentButtonUiState {
    data object Writer : PostContentButtonUiState
    data object Supporter : PostContentButtonUiState
}

data class DialogUiState(
    val title: String?,
    val message: String?,
    val recruitItem: PostContentItem.RecruitItem?
) {
    companion object {
        fun init() = DialogUiState(
            title = null,
            message = null,
            recruitItem = null
        )
    }
}