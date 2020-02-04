package com.julien.findapro.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import com.julien.findapro.R
import com.julien.findapro.controller.activity.AssignmentDetailActivity
import com.julien.findapro.controller.activity.AssignmentsChoiceActivity
import com.julien.findapro.controller.activity.ChatActivity

class FirebaseMessagingService: FirebaseMessagingService() {

    //when notification create for user in firebase
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

        }

        //send notification
        createNotification(applicationContext,remoteMessage.data["titleNotification"].toString(),remoteMessage.data["textNotification"].toString(),remoteMessage.data["cause"].toString(),remoteMessage.data["assignmentId"].toString())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token: String?) {

        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }


    private fun createNotification(
        context: Context,
        notificationTitle: String,
        notificationDescription: String,
        cause:String,
        assignmentId:String
    ) {

        val intent:Intent?


        when (cause) {
            "new message" -> {
                intent = Intent(this, ChatActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                intent.putExtra("assignment",assignmentId)
            }
            "new assignment created" -> {
                intent = Intent(
                    this,
                    AssignmentsChoiceActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                intent.putExtra("id",assignmentId)
            }
            else -> {
                intent = Intent(this, AssignmentDetailActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                intent.putExtra("id",assignmentId)
            }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        createNotificationChannel(context)


        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.icon)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(2, builder.build())
        }

    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name ="chanelName"
            val descriptionText = "test description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    companion object {

        private const val CHANNEL_ID = "notification_channel_id"
        private const val TAG = "FirebaseMsgService"
    }
}