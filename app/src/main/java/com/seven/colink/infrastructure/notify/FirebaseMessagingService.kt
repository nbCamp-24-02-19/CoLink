package com.seven.colink.infrastructure.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.seven.colink.R
import com.seven.colink.domain.model.NotifyType
import com.seven.colink.ui.chat.ChatRoomActivity
import com.seven.colink.ui.group.GroupActivity
import com.seven.colink.ui.main.MainActivity
import com.seven.colink.ui.post.register.PostActivity
import com.seven.colink.util.convert.convertTime
import com.seven.colink.util.loadImageBitmap
import com.seven.colink.util.status.GroupType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseMessagingService : FirebaseMessagingService() {

    // 메세지가 수신되면 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 서버에서 직접 보낼때
        if (remoteMessage.notification != null) {
            sendNotification(
                remoteMessage.notification?.title,
                remoteMessage.notification?.body!!
            )
        }
        //다른 기기에서 서버로 보낼때
        else if (remoteMessage.data.isNotEmpty()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                CoroutineScope(SupervisorJob()).launch {
                    remoteMessage.data["key"]?.let { key ->
                        remoteMessage.data["type"]?.let { type ->
                            remoteMessage.data["title"]?.let { title ->
                                remoteMessage.data["message"]?.let { message ->
                                    remoteMessage.data["groupType"]?.let { groupType ->
                                        when (NotifyType.fromTitle(type)) {
                                            NotifyType.CHAT -> {
                                                remoteMessage.data["name"]?.let { name ->
                                                    remoteMessage.data["img"]?.let { img ->
                                                        remoteMessage.data["registeredDate"]?.let { registeredDate ->
                                                            sendNotification(
                                                                key = key,
                                                                title = title,
                                                                name = name,
                                                                message = message,
                                                                img = img,
                                                                type = type,
                                                                registeredDate = registeredDate
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            NotifyType.INVITE ->
                                                sendNotification(
                                                    key = key,
                                                    title = title,
                                                    type = type,
                                                    message = message,
                                                    groupType = GroupType.from(groupType.toInt()),
                                                )

                                            NotifyType.APPLY ->
                                                sendNotification(
                                                    key = key,
                                                    title = title,
                                                    type = type,
                                                    message = message,
                                                    groupType = GroupType.from(groupType.toInt()),
                                                )

                                            NotifyType.JOIN ->
                                                sendNotification(
                                                    key = key,
                                                    title = title,
                                                    type = type,
                                                    message = message,
                                                    groupType = GroupType.from(groupType.toInt()),
                                                )

                                            else -> {
                                                sendNotification(
                                                    remoteMessage.notification?.title,
                                                    remoteMessage.notification?.body ?: return@launch
                                                )
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                sendNotification(
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body!!
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun sendNotification(
        key: String,
        title: String,
        name: String,
        message: String,
        img: String,
        type: String,
        registeredDate: String
    ) = withContext(Dispatchers.Main) {
        val code = key.hashCode()
        val intent = ChatRoomActivity.newIntent(this@FirebaseMessagingService, key, title)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = pendingIntent(code, intent)

        val user: Person = Person.Builder()
            .setName(name)
            .setIcon(
                IconCompat.createWithResource(
                    this@FirebaseMessagingService,
                    R.drawable.ic_colink_chat
                )
            )
            .build()

        val notifyMessage = NotificationCompat.MessagingStyle.Message(
            message,
            System.currentTimeMillis(),
            user
        )

        val channelId = "channel_$type$key"

        val messageStyle = NotificationCompat.MessagingStyle(user)
            .addMessage(notifyMessage)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 소리

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationLayout =
            RemoteViews("com.seven.colink", R.layout.util_custom_chat_notify_small)
        val notificationLayoutExpanded =
            RemoteViews("com.seven.colink", R.layout.util_custom_chat_notify_large)

        notificationLayout.apply {
            setTextViewText(R.id.notification_title, name)
            setTextViewText(R.id.notification_description, message)
        }

        notificationLayoutExpanded.apply {
            if (title == name) setViewVisibility(R.id.notification_chat_title, View.GONE)
            setTextViewText(R.id.notification_chat_title, title)
            setTextViewText(R.id.notification_chat_name, name)
            setTextViewText(R.id.notification_chat_description, message)
            setTextViewText(R.id.notification_chat_time, registeredDate.convertTime())
        }

        withContext(Dispatchers.IO) {
            val bitmap = this@FirebaseMessagingService.loadImageBitmap(img)
            withContext(Dispatchers.Main) {
                notificationLayoutExpanded.setImageViewBitmap(
                    R.id.notification_thumbnail,
                    bitmap
                )

                val notificationBuilder =
                    NotificationCompat.Builder(this@FirebaseMessagingService, channelId)
                        .setContentTitle(title) // 제목
                        .setContentText(message) // 내용
                        .setStyle(messageStyle)
                        .setSmallIcon(R.drawable.ic_colink_chat) // 아이콘
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setCustomContentView(notificationLayout)
                        .setCustomBigContentView(notificationLayoutExpanded)

                val channel = NotificationChannel(
                    channelId,
                    "알림 메세지",
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(channel)

                notificationManager.notify(code, notificationBuilder.build()) // 알림 생성
            }
        }
    }

    private suspend fun sendNotification(
        key: String,
        title: String,
        type: String,
        message: String,
        groupType: GroupType,
    ) = withContext(Dispatchers.Main){
        val code = key.hashCode()
        var intent = Intent()
            intent =
                when (NotifyType.fromTitle(type)) {
                    NotifyType.APPLY -> GroupActivity.newIntent(
                        this@FirebaseMessagingService,
                        groupType,
                        key = key
                    )

                    else -> PostActivity.newIntent(
                        this@FirebaseMessagingService,
                        groupType,
                        key = key
                    )
                }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = pendingIntent(code, intent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val icon = when (NotifyType.fromTitle(type)) {
            NotifyType.INVITE -> R.drawable.ic_invite
            NotifyType.JOIN -> R.drawable.ic_join
            NotifyType.APPLY -> R.drawable.ic_apply_request
            else -> return@withContext
        }
        val channelId = "channel_$type$key"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this@FirebaseMessagingService, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(icon)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)


        val channel = NotificationChannel(
            "channel_$type$key",
            "알림 메세지",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(code, notificationBuilder.build())
    }

    private fun sendNotification(title: String?, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복 생성 방지
        val pendingIntent = pendingIntent(0, intent)

        val channelId = "channel" // 채널 아이디
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 소리
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title) // 제목
            .setContentText(body) // 내용
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, notificationBuilder.build()) // 알림 생성
    }

    private fun pendingIntent(code: Int, intent: Intent) =
        PendingIntent.getActivity(
            this, code, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}