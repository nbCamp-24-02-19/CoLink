package com.seven.colink.data.firebase.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.domain.repository.ScheduleRepository
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): ScheduleRepository {
    suspend fun
}