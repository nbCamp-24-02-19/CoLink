package com.seven.colink.infrastructure.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.seven.colink.ui.main.MainActivity
import com.seven.colink.util.convert.convertTime
import com.seven.colink.util.convert.convertToDaysAgo
import com.seven.colink.util.loadImageBitmap
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
                remoteMessage.data["title"]?.let { title ->
                    remoteMessage.data["name"]?.let { name ->
                        remoteMessage.data["message"]?.let { message ->
                            remoteMessage.data["img"]?.let { img ->
                                remoteMessage.data["type"]?.let { type ->
                                    remoteMessage.data["registeredDate"]?.let { registeredDate ->
                                        sendNotification(title, name, message, img, type, registeredDate)
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
    private fun sendNotification(
        title: String,
        name: String,
        message: String,
        img: String,
        type: String,
        registeredDate: String
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val user: Person = Person.Builder()
            .setName(name)
            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_colink_chat))
            .build()

        val notifyMessage = NotificationCompat.MessagingStyle.Message(
            message,
            System.currentTimeMillis(),
            user
        )

        val messageStyle = NotificationCompat.MessagingStyle(user)
            .addMessage(notifyMessage)

        val channelId = "channel_$type"
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

        CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
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
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)

                notificationManager.notify(0, notificationBuilder.build()) // 알림 생성
            }
        }
    }

    private fun sendNotification(title: String?, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복 생성 방지
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        ) // 일회성

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
}

/*
* 알림 요청(notification request)을 만들어 푸시 알림 서비스(FCM)로 보내주는 주체.
알림 요청을 만들기 위해서는 다음과 같은 데이터가 필요하다.
단말 토큰 (device token): 알림 요청을 보내는 데 필요한 고유 식별자.
페이로드 (payload): 알림 내용을 담은 JSON 딕셔너리
*
* 앱이 FCM 서버와 통신하기 위해 사용되는 고유한 식별자
앱은 서버와 통신할 때 토큰을 사용하여 FCM 서버에서 앱을 식별하고, 이를 통해 메시지 전송을 할수 있다.
FCM의 토큰은 앱이 설치된 디바이스마다 고유하다. 앱이 설치된 디바이스를 추가하거나 삭제할 때 토큰이 변경될 수 있다. (refresh)
서버는 이러한 FCM 토큰을 사용하여 특정 디바이스에 메시지를 전송할 수 있다.
*
* FCM의 TOPIC
토픽(Topic)은 일종의 채널로서, 이를 통해 일련의 수신자들에게 메시지를 전송할 수 있다.
구독 및 구독취소 요청 시, FCM은 구독한 유저들을 내부적으로 관리한다.
subscribe, unsubscribe 메서드를 통해 구독과 구독 취소 요청을 FCM에 전송할 수 있다.
토픽을 통한 푸시 발송시, 토픽을 구독한 사용자들에게 메시지를 전송할 수 있다.
* */