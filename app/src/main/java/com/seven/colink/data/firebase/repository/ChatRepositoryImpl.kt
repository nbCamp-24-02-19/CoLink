package com.seven.colink.data.firebase.repository

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.seven.colink.data.firebase.type.DataBaseType
import com.seven.colink.domain.entity.ChatRoomEntity
import com.seven.colink.domain.entity.MessageEntity
import com.seven.colink.domain.repository.ChatRepository
import com.seven.colink.ui.chat.type.ChatTabType
import com.seven.colink.util.status.DataResultStatus
import kotlinx.coroutines.internal.resumeCancellableWith
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
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

    override suspend fun getChatRoomList(userId: String, type: ChatTabType) = runCatching {
        val chatRoomList = mutableListOf<ChatRoomEntity>()
        val snapshot = db.reference.child(DataBaseType.CHATROOM.title)
            .get()
            .await()

        for (childSnapshot in snapshot.children) {
            val chatRoom = childSnapshot.getValue(ChatRoomEntity::class.java)
            if (chatRoom != null && chatRoom.type == type) {
                chatRoomList.add(chatRoom)
            }
        }
        chatRoomList.toList()
    }.onFailure { exception ->
        Log.e("getChatRoomList", "Exception while getting chat room list", exception)
    }
    override suspend fun getChatRoomMessage(
        chatRoomId: String,
        ) = suspendCancellableCoroutine {  continuation ->
        db.reference.child(DataBaseType.MESSAGE.title).child(chatRoomId)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot)
                    = continuation.resume(snapshot.children.mapNotNull { it.getValue(MessageEntity::class.java) })

                    override fun onCancelled(error: DatabaseError)
                    = if (error.message == "Permission denied") {
                        continuation.resume(null)
                    } else {
                        continuation.resumeWithException(RuntimeException(error.message))
                    }
                }
            )
    }

    override suspend fun updateMessage(newMessage: MessageEntity) {
        db.reference.child(DataBaseType.MESSAGE.title).child(newMessage.chatRoomId).child(newMessage.key)
            .setValue(newMessage)
    }
    override suspend fun sendMessage(message: MessageEntity) {
        val messageRef = db.reference.child(DataBaseType.MESSAGE.title).child(message.chatRoomId).push()
        messageRef.setValue(message.copy(key = messageRef.key!!))
    }

    override suspend fun observeNewMessage(
        chatRoom: ChatRoomEntity,
        callback: (MessageEntity) -> Unit
    ) {
        updateChatParticipants(chatRoom)
        db.reference.child(DataBaseType.MESSAGE.title)
            .child(chatRoom.key)
            .addChildEventListener(
                object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot.getValue(MessageEntity::class.java)?.let { callback(it) }
                    }
                    override fun onChildChanged(snapshot: DataSnapshot,previousChildName: String?) = Unit
                    override fun onChildRemoved(snapshot: DataSnapshot) = Unit
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("observeNewMessages", "ERROR: $error")
                    }
                })
    }

    private fun updateChatParticipants(chatRoom: ChatRoomEntity) {
        chatRoom.participantsUid.forEach { (uid, _) ->
            firestore.runTransaction { transaction ->
                val ref = firestore.collection(DataBaseType.USER.title).document(uid)
                val snapshot = transaction.get(ref)
                val currentList = snapshot.get("participantsChatRoomIds") as? List<String> ?: emptyList()
                if (!currentList.contains(chatRoom.key)) {
                    val updatedChatRoomKeyList = currentList + chatRoom.key
                    transaction.update(ref, "participantsChatRoomIds", updatedChatRoomKeyList)
                }
            }
        }
    }

}