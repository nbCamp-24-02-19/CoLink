package com.seven.colink.infrastructure.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.seven.colink.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirebaseMessagingService: FirebaseMessagingService() {

    // 메세지가 수신되면 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.data.isNotEmpty()){
            sendNotification(remoteMessage.notification?.title,
                remoteMessage.notification?.body!!)
            Log.d("alram", "do $remoteMessage")
        }
        else{

        }
    }

    // Firebase Cloud Messaging Server 가 대기중인 메세지를 삭제 시 호출
    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    // 메세지가 서버로 전송 성공 했을때 호출
    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }

    // 메세지가 서버로 전송 실패 했을때 호출
    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)
    }

    // 새로운 토큰이 생성 될 때 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)
    }

    private fun sendNotification(title: String?, body: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복 생성 방지
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE) // 일회성

        val channelId = "channel" // 채널 아이디
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 소리
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title) // 제목
            .setContentText(body) // 내용
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0 , notificationBuilder.build()) // 알림 생성
    }

    // 받은 토큰을 서버로 전송
    private fun sendRegistrationToServer(token: String) {
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