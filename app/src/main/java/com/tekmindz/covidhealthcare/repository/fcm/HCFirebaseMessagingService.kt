package com.tekmindz.covidhealthcare.repository.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tekmindz.covidhealthcare.HomeActivity
import com.tekmindz.covidhealthcare.R
import java.util.*

class HCFirebaseMessagingService : FirebaseMessagingService() {
    private var TAG = "HCFirebaseMessagingService"
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG,"New Token : "+p0)
        //save locally or / send token to server
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FROM : " + remoteMessage!!.from)

        //Verify if the message contains data
        if (remoteMessage.data != null) {
            if (remoteMessage.data.isNotEmpty()) {
                Log.d(TAG, "Message data : " + remoteMessage.data)
                val data = remoteMessage.data
                sendNotification(data)

            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }
    }

    private fun sendNotification(data: Map<String, String>) {
        try {
            val channelId = getString(R.string.default_notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.notification_icon)
            val mBuilder = NotificationCompat.Builder(this, channelId)
            val intent = Intent(this, HomeActivity::class.java) //add screen class name to move on when user click on notification
            val pendingIntent = PendingIntent.getActivity(
                this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
            )
            var title = ""
            var body = ""
            val id = Date().time.toInt()
            if (data.containsKey("title")) {
                title = data.get("title").toString()
            }
            if (data.containsKey("body")) {
                body = data.get("body").toString()
            }
            mBuilder.setTicker(getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setSound(defaultSoundUri)
                .setOnlyAlertOnce(false)
                // .setSmallIcon(R.mipmap.notification_icon)
                .setLargeIcon(largeIcon)
                .setGroupSummary(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText(body)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.mipmap.notification_icon)
                mBuilder.setColor(ActivityCompat.getColor(this,R.color.colorPrimaryDark))
            } else {
                mBuilder.setSmallIcon(R.mipmap.notification_icon)
            }
            mBuilder.build()
            val style: NotificationCompat.Style
            val bigTextStyle = NotificationCompat.BigTextStyle()
            style = bigTextStyle
            mBuilder.setContentText(body)
            bigTextStyle.bigText(body)
            mBuilder.setStyle(style)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // notification channel in android Oreo is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(id, mBuilder.build())
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

}