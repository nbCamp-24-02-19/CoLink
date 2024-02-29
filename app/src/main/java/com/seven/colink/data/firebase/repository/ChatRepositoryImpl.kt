package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.internal.resumeCancellableWith
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.RuntimeException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
) : ChatRepository {

    override suspend fun createChatRoom(chatRoom: ChatRoomEntity) {
        db.reference.child(DataBaseType.CHATROOM.title).child(chatRoom.key).setValue(chatRoom)
    }

    override suspend fun getChatRoom(chatRoomId: String) = suspendCoroutine { continuation ->
        db.reference.child(DataBaseType.CHATROOM.title).child(chatRoomId)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        continuation.resume(snapshot.getValue(ChatRoomEntity::class.java))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(null)
                    }

                }
            )
    }

    override suspend fun deleteChatRoom(chatRoomId: String) {
        db.reference.child(DataBaseType.CHATROOM.title).child(chatRoomId).removeValue()
    }

    override suspend fun getChatRoomList(userId: String) = runCatching {
        db.reference.child(DataBaseType.CHATROOM.title)
    }
    override suspend fun getChatRoomMessage(chatRoomId: String) = suspendCancellableCoroutine {  continuation ->
        db.reference.child(DataBaseType.MESSAGE.title).child(chatRoomId)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot)
                    = continuation.resume(snapshot.children.mapNotNull { it.getValue(MessageEntity::class.java) })

                    override fun onCancelled(error: DatabaseError)
                    = continuation.resumeWithException(RuntimeException(error.message))
                }
            )
    }

    override suspend fun sendMessage(message: MessageEntity) {
        db.reference.child(DataBaseType.MESSAGE.title).child(message.chatRoomId).child(message.key)
            .push().setValue(message)
    }

    override suspend fun observeMessages(
        chatRoom: ChatRoomEntity,
        callback: (List<MessageEntity>) -> Unit
    ) {
        db.reference.child(DataBaseType.MESSAGE.title)
            .child(chatRoom.key)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messages =
                            snapshot.children.mapNotNull { it.getValue(MessageEntity::class.java) }
                        chatRoom.participantsUid.forEach { uid ->
                            firestore.runTransaction { transaction ->
                                val ref =
                                    firestore.collection(DataBaseType.USER.title).document(uid)
                                val snapshot = transaction.get(ref)
                                val currentList =
                                    snapshot.get("chatRoomKeyList") as? List<String> ?: emptyList()
                                if (!currentList.contains(chatRoom.key)) {
                                    val updatedChatRoomKeyList = currentList + chatRoom.key
                                    transaction.update(
                                        ref,
                                        "chatRoomKeyList",
                                        updatedChatRoomKeyList
                                    )
                                }
                            }
                        }
                        callback(messages)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ChatRepo_observeMessage", "error: $error")
                    }
                })
    }

}