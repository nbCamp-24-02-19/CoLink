package com.seven.colink.data.firebase.mapper

import com.seven.colink.domain.entity.UserEntity

fun UserEntity.toFirestore(): Map<String, Any?> = mapOf(
    "uid" to this.uid,
    "email" to this.email,
    "id" to this.id,
    "name" to this.name,
    "photoUrl" to this.photoUrl,
    "level" to this.level,
    "specialty" to this.specialty,
    "grade" to this.grade,
    "skill" to this.skill,
    "blog" to this.blog,
    "info" to (this.info ?: ""),
    "joinDate" to this.joinDate
)