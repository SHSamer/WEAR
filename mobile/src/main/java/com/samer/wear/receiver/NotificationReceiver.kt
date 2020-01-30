package com.samer.wear.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.samer.wear.R
import com.samer.wear.db.contract.EventType
import com.samer.wear.db.helper.EventDBHelper
import com.samer.wear.service.notification.NotificationAction

class NotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive")

        val events = EventDBHelper(context).readAllNowEvents()

        for (event in events)
        {
            if(!event.notification)
                continue

            NOTIFICATION_ID++

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val dismissIntent = Intent(context, NotificationAction::class.java)
            dismissIntent.action = NotificationAction.ACTION_DISMISS

            dismissIntent.putExtra("event", event)
            dismissIntent.putExtra("notification_id", NOTIFICATION_ID)

            val dismissPendingIntent = PendingIntent.getService(context, 0, dismissIntent, 0)
            val dismissAction = android.support.v4.app.NotificationCompat.Action.Builder(
                    R.drawable.ic_check,
                    "Ok",
                    dismissPendingIntent)
                    .build()


            val reportShortIntent = Intent(context, NotificationAction::class.java)
            reportShortIntent.action = NotificationAction.ACTION_REPORT_SHORT

            reportShortIntent.putExtra("event", event)
            reportShortIntent.putExtra("notification_id", NOTIFICATION_ID)

            val reportShortPendingIntent = PendingIntent.getService(context, 0, reportShortIntent, 0)

            val reportShortAction = android.support.v4.app.NotificationCompat.Action.Builder(
                    R.drawable.ic_alarm,
                    "Report : 5 minutes",
                    reportShortPendingIntent)
                    .build()


            val notificationCompatBuilder = NotificationCompat.Builder(context, CHANNEL_ID)

            val buildedNotification = notificationCompatBuilder
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Evenement Aujourd'hui : " + event.name)
                    .setContentText(event.description)
                    .setChannelId(CHANNEL_ID)
                    .addAction(dismissAction)
                    .addAction(reportShortAction)
                    //.addAction(reportlongAction)

            if (event.type == EventType.eventTypes[0]) {
                val startSportActivityIntent = Intent(context, NotificationAction::class.java)
                startSportActivityIntent.action = NotificationAction.ACTION_START_SPORT_ACTIVITY

                startSportActivityIntent.putExtra("event", event)
                startSportActivityIntent.putExtra("notification_id", NOTIFICATION_ID)

                val startSportActivityPendingIntentService = PendingIntent.getService(context, 0, startSportActivityIntent, 0)
                val startSportActivitygAction = android.support.v4.app.NotificationCompat.Action.Builder(
                        R.drawable.ic_walk,
                        "Commencer activité sportive",
                        startSportActivityPendingIntentService)
                        .build()


                buildedNotification.addAction(startSportActivitygAction)
            }

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "YOUR_CHANNEL_ID"
                val channel = NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT)
                mNotificationManager.createNotificationChannel(channel)
                buildedNotification.setChannelId(channelId)
            }

            mNotificationManager.notify(0, buildedNotification.build())
        }
    }

    companion object {
        const val TAG = "NotificationReceiver"
        var NOTIFICATION_ID = 0
        private const val CHANNEL_ID = "notify_001"
    }
}