package com.tekmindz.covidhealthcare.repository.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tekmindz.covidhealthcare.HomeActivity
import com.tekmindz.covidhealthcare.R
import com.tekmindz.covidhealthcare.application.App.Companion.isForeGround
import com.tekmindz.covidhealthcare.constants.Constants.BROADCAST_RECEIVER_NAME
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_BED_NUMBER
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_MESSAGE
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_NOTIFICATION_ID
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_NOTIFICATION_TIME
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_OBSERVATION_TYPE
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_OBSERVATION_VALUE
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_PATIENT_ID
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_PATIENT_NAME
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_STATUS
import com.tekmindz.covidhealthcare.constants.Constants.EXTRA_WARD_NUMBER
import com.tekmindz.covidhealthcare.constants.Constants.PATIENT_ID
import com.tekmindz.covidhealthcare.utills.Utills
import java.util.*

class HCFirebaseMessagingService : FirebaseMessagingService() {
    private var TAG = "HCFirebaseMessagingService"
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "New Token : " + p0)
        //save locally or / send token to server
    }

    lateinit var mbroadCastreceiver: LocalBroadcastManager

    @Override
    override fun onCreate() {
        mbroadCastreceiver = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FROM : " + remoteMessage.from)

        //Verify if the message contains data
        if (remoteMessage.data != null) {
            if (remoteMessage.data.isNotEmpty()) {
                Log.d(TAG, "Message data : " + remoteMessage.data)
                val data = remoteMessage.data
                if (isForeGround && Utills.destination == "Notifications" && remoteMessage.data.containsKey(
                        PATIENT_ID
                    )
                ) {
                    Log.e("patientId", "${remoteMessage.data.get(PATIENT_ID)}")
                    val intent = Intent(BROADCAST_RECEIVER_NAME)
                    intent.putExtra(EXTRA_PATIENT_ID, remoteMessage.data.get(EXTRA_PATIENT_ID))
                    intent.putExtra(
                        EXTRA_OBSERVATION_TYPE,
                        remoteMessage.data.get(EXTRA_OBSERVATION_TYPE)
                    )
                    intent.putExtra(
                        EXTRA_OBSERVATION_VALUE,
                        remoteMessage.data.get(EXTRA_OBSERVATION_VALUE)
                    )
                    intent.putExtra(EXTRA_STATUS, remoteMessage.data.get(EXTRA_STATUS))
                    intent.putExtra(EXTRA_PATIENT_NAME, remoteMessage.data.get(EXTRA_PATIENT_NAME))
                    intent.putExtra(EXTRA_WARD_NUMBER, remoteMessage.data.get(EXTRA_WARD_NUMBER))
                    intent.putExtra(EXTRA_BED_NUMBER, remoteMessage.data.get(EXTRA_BED_NUMBER))
                    intent.putExtra(EXTRA_MESSAGE, remoteMessage.data.get(EXTRA_MESSAGE))
                    intent.putExtra(
                        EXTRA_NOTIFICATION_TIME,
                        remoteMessage.data.get(EXTRA_NOTIFICATION_TIME)
                    )
                    intent.putExtra(
                        EXTRA_NOTIFICATION_ID,
                        remoteMessage.data.get(EXTRA_NOTIFICATION_ID)
                    )

                    mbroadCastreceiver.sendBroadcast(intent)

                } else {
                    sendNotification(data)
                }
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
            val intent = Intent(
                this,
                HomeActivity::class.java
            ) //add screen class name to move on when user click on notification
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

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
                .setLights(Color.RED, 300, 300)
                .setContentIntent(pendingIntent)
                .setContentText(body)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.mipmap.notification_icon)
                mBuilder.color = ActivityCompat.getColor(this, R.color.colorPrimaryDark)
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
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}