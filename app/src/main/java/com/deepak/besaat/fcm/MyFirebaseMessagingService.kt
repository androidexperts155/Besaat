package com.deepak.besaat.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.deepak.besaat.R
import com.deepak.besaat.view.activities.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@SuppressLint("Registered")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    var notifManager: NotificationManager? = null
    internal val NotificationID = 1
    internal var CHANNEL_ID = "com.Bissat"
    internal var CHANNEL_NAME = "Bissat"
    internal var CHANNEL_DESCRIPTION = "https://www.Bissat.com/"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("fcm", "From: ${remoteMessage?.from}")
        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.d("fcm", "Message data payload: " + remoteMessage.data)
        }
        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Log.d("fcm", "Message Notification Body: ${it.body}")
            sendNotification(remoteMessage?.notification!!.body!!)
        }
//        sendNotification("")
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("fcm", "refreshedToken: $token")
    }

    internal fun sendNotification(message: String) {
        //////////////////////////////////////////////////
        if (notifManager == null) {
            notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            mChannel.description = CHANNEL_DESCRIPTION
            mChannel.enableLights(true)
            mChannel.lightColor = Color.MAGENTA
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            val i: Intent
            i = Intent(applicationContext, HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT)
            val notificationnn = Notification.Builder(applicationContext)
                .setContentTitle("Bissat")
                .setContentText(message)
//                .setSmallIcon(R.drawable.icn_notification_logo)
//                .setBadgeIconType(R.drawable.icn_notification_logo)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.FLAG_AUTO_CANCEL)
                .setColor(Color.parseColor("#31AEE6"))
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            if (mNotificationManager != null) {
                Log.e("aaa", "-=-=-= no createing notifacation -=-==")
                mNotificationManager.createNotificationChannel(mChannel)
            } else {
                Log.e("aaa", "-=-=-= created notifacation -=-==")
            }
            mNotificationManager.notify(NotificationID, notificationnn)
        } else {
            val i: Intent
            //            if (to_intent.equals("chat")) {
            i = Intent(applicationContext, HomeActivity::class.java)
            //                i.putExtra("data", userDatum);
            //            } else {
            //                i = new Intent(getApplicationContext(), NotificationsActivity.class);
            //            }
            ////        i.putExtra("data", "hell");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT)
            val notificationBuilder = NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.icn_notification_logo)
                .setContentTitle("Bissat")
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setContentIntent(pendingIntent)
                //                .setWhen(getTimeMilliSec(timeStamp))
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.FLAG_AUTO_CANCEL)
                .setColor(Color.parseColor("#31AEE6"))
                .setPriority(Notification.PRIORITY_HIGH)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NotificationID, notificationBuilder.build())
        }

        //////////////////////////////////////////////////
    }
}