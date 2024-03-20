package com.seven.colink.data.firebase.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.seven.colink.domain.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class UserRepositoryImplTest {
    private lateinit var userRepository: UserRepositoryImpl
    private val firebaseAuth: FirebaseAuth = mock(FirebaseAuth::class.java)
    private val firebaseFirestore: FirebaseFirestore = mock(FirebaseFirestore::class.java)
    private val documentSnapshot: DocumentSnapshot = mock(DocumentSnapshot::class.java)
    private val firebaseMessaging: FirebaseMessaging = mock(FirebaseMessaging::class.java)

    @BeforeEach
    fun setUp() {
        userRepository = UserRepositoryImpl(firebaseAuth, firebaseFirestore, firebaseMessaging)
    }

    @Test
    fun `registUser should call set on Firestore`() {
        // Given
        val user = UserEntity(
            uid = "user123",
            email = "zxcasd@wdsad.com",
            name = "John Doe",
            photoUrl = "https://example.com/photo.jpg",
            phoneNumber = "123-456-7890",
            level = 5,
            specialty = "Mobile Development",
            grade = 4.5,
            skill = listOf("Kotlin", "Swift", "Flutter"),
            info = "Experienced mobile developer",
            registeredDate = "2022-01-01")
        val mockCollection = mock(CollectionReference::class.java)
        val mockDocument = mock(DocumentReference::class.java)
        `when`(firebaseFirestore.collection(anyString())).thenReturn(mockCollection)
        `when`(mockCollection.document(anyString())).thenReturn(mockDocument)

        // When
        runBlocking {
            userRepository.registerUser(user)
        }

        // Then
        verify(mockDocument).set(any(UserEntity::class.java))
    }

    @Test
    fun `getUser should return UserEntity when user exists`() {
        // Given
        val userId = "userId"
        val mockCollection = mock(CollectionReference::class.java)
        val mockDocument = mock(DocumentReference::class.java)
        `when`(firebaseFirestore.collection(anyString())).thenReturn(mockCollection)
        `when`(mockCollection.document(anyString())).thenReturn(mockDocument)
        `when`(mockDocument.get()).thenReturn(Tasks.forResult(documentSnapshot))
        `when`(documentSnapshot.exists()).thenReturn(true)
        `when`(documentSnapshot.toObject(UserEntity::class.java)).thenReturn(UserEntity(
            uid = "user123",
            email = "zxcasd@wdsad.com",
            name = "John Doe",
            photoUrl = "https://example.com/photo.jpg",
            phoneNumber = "123-456-7890",
            level = 5,
            specialty = "Mobile Development",
            grade = 4.5,
            skill = listOf("Kotlin", "Swift", "Flutter"),
            info = "Experienced mobile developer",
            registeredDate = "2022-01-01"
        ))

        // When
        val result = runBlocking { userRepository.getUserDetails(userId) }

        // Then
        assertNotNull(result)
    }
}