package com.seven.colink.domain.entity

import com.google.firebase.firestore.Exclude
import com.seven.colink.util.convert.convertLocalDateTime
import java.time.LocalDateTime

data class UserEntity(
    val uid: String? = "",
    val email: String? = "",
    val name: String? = "",
    val photoUrl: String = "https://firebasestorage.googleapis.com/v0/b/colink-a7c3a.appspot.com/o/img%2Fic_profile.png?alt=media&token=64207054-6439-4275-b7be-cd13c626f081",
    val phoneNumber: String? = null,
    val level: Int? = 0,
    val mainSpecialty: String? = "",
    val specialty: String? = "",
    val grade: Double? = 5.0,
    val skill: List<String>? = emptyList(),
    val git: String? = null,
    val blog: String? = null,
    val link: String? = null,
    val info: String? = null,
    val registeredDate: String = LocalDateTime.now().convertLocalDateTime(),
    val communication: Int? = null,
    val technicalSkill: Int? = null,
    val diligence: Int? = null,
    val flexibility: Int? = null,
    val creativity: Int? = null,
    val evaluatedNumber: Int = 0,
    val participantsChatRoomIds: List<String>? = emptyList(),
    @get:Exclude @set:Exclude var chatRoomKeyList: List<String>? = null
)
