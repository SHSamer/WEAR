package com.samer.wear.service.notification

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import com.samer.wear.R
import com.samer.wear.activity.MainActivity
import com.samer.wear.db.contract.EventType
import com.samer.wear.db.helper.EventDBHelper
import com.samer.wear.db.model.Event


class NotificationAction : IntentService("NotificationAction") {
    companion object {
        const val TAG = "NotificationIntentServi" //23char

        const val ACTION_DISMISS = "com.samer.service.NotificationAction.DismissAction"
        const val ACTION_REPORT_SHORT = "com.samer.service.NotificationAction.ActionReportShort"
        const val ACTION_REPORT_LONG = "com.samer.service.NotificationAction.ActionReportLongAction"
        const val ACTION_START_SPORT_ACTIVITY = "come.samer.service.NotificationAction.ActionStartSportActivity"

        private const val START_ACTIVITY_PATH = "/start-activity"

        private const val CHANNEL_ID = "notify_001"
    }

    val REPORT_TIME_SHORT = 300000L
    val REPORT_TIME_LONG = 3600000L

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var mNode: Node

    override fun onHandleIntent(intent: Intent?) {

        eventDBHelper = EventDBHelper(this)

        Log.d(TAG, "onHandleIntent(): " + intent!!)

        var event = intent.getSerializableExtra("event") as Event
        var notificationID = intent.getIntExtra("notification_id", -1)

        val action = intent.action

        if (ACTION_DISMISS.equals(action))
            handleActionDismiss(event, notificationID)
        else if (ACTION_REPORT_SHORT.equals(action))
            handleActionReport(event, REPORT_TIME_SHORT, notificationID)
        else if (ACTION_REPORT_LONG.equals(action))
            handleActionReport(event, REPORT_TIME_LONG, notificationID)
        else if (ACTION_START_SPORT_ACTIVITY.equals(action))
            handleActionStartSportActivity(event, notificationID)
    }

    private fun handleActionDismiss(event: Event, notificationID: Int) {
        Log.d(TAG, "handleActionDismiss")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        notificationManagerCompat.cancel(notificationID)

        eventDBHelper.updateNotification(event.id, 0)
    }

    private fun handleActionReport(event: Event, reportTime: Long, notificationID: Int) {
        Log.d(TAG, "handleActionReport")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        notificationManagerCompat.cancel(notificationID)

        try {
            Thread.sleep(reportTime)
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        notificationManagerCompat.notify(notificationID, createNotification(event, notificationID))
    }

    private fun handleActionStartSportActivity(event: Event, notificationID: Int) {
        Log.d(TAG, "handleActionStartSportActivity")

        val notificationManagerCompat = NotificationManagerCompat.from(applicationContext)

        notificationManagerCompat.cancel(notificationID)

        eventDBHelper.updateNotification(event.id, 0)

        resolveNode()
    }

    private fun resolveNode() {
        Log.d(TAG, "resolveNode")

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build()

        mGoogleApiClient.connect()

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback(object : ResultCallback<NodeApi.GetConnectedNodesResult> {
                    override fun onResult(connectedNodes: NodeApi.GetConnectedNodesResult) {
                        for (connectedNode in connectedNodes.nodes) {
                            mNode = connectedNode
                            sendMessage(START_ACTIVITY_PATH, "")
                        }
                    }
                })
    }


    private fun sendMessage(subject: String, message: String) {
        Log.d(TAG, "sendMessage")

        Wearable.MessageApi.sendMessage(mGoogleApiClient,
                mNode.id,
                subject,
                message.toByteArray())
                .setResultCallback { sendMessageResult ->
                    if (sendMessageResult.status.isSuccess)
                        Log.d(TAG, "Message sended")
                    else
                        Log.e(TAG, "Message not sended")
                }
    }

    private fun createNotification(event: Event, notificationID: Int): Notification {
        Log.d(TAG, "createNotification")

        var intent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val dismissIntent = Intent(this, NotificationAction::class.java)
        dismissIntent.action = ACTION_DISMISS

        dismissIntent.putExtra("event", event)
        dismissIntent.putExtra("notification_id", notificationID)

        val dismissPendingIntent = PendingIntent.getService(this, 0, dismissIntent, 0)
        val dismissAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.drawable.ic_check,
                "Ok",
                dismissPendingIntent)
                .build()


        val reportShortIntent = Intent(this, NotificationAction::class.java)
        reportShortIntent.action = ACTION_REPORT_SHORT

        reportShortIntent.putExtra("event", event)
        reportShortIntent.putExtra("notification_id", notificationID)

        val reportShortPendingIntent = PendingIntent.getService(this, 0, reportShortIntent, 0)

        val reportShortAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.mipmap.ic_clock,
                "Report : 5 minutes",
                reportShortPendingIntent)
                .build()

        val reportLongIntent = Intent(this, NotificationAction::class.java)
        reportLongIntent.action = ACTION_REPORT_LONG

        reportLongIntent.putExtra("event", event)
        reportLongIntent.putExtra("notification_id", notificationID)

        val reportLongPendingIntentService = PendingIntent.getService(this, 0, reportLongIntent, 0)
        val reportlongAction = android.support.v4.app.NotificationCompat.Action.Builder(
                R.mipmap.ic_clock,
                "Report : 1 heure",
                reportLongPendingIntentService)
                .build()

        val notificationCompatBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)

        var buildedNotification = notificationCompatBuilder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Evenement Aujourd'hui : " + event.name)
                .setContentText(event.description)
                .setChannelId(CHANNEL_ID)
                .addAction(dismissAction)
                .addAction(reportShortAction)
                .addAction(reportlongAction)

        if (event.type == EventType.eventTypes[0]) {
            val startSportActivityIntent = Intent(this, NotificationAction::class.java)
            startSportActivityIntent.action = ACTION_START_SPORT_ACTIVITY

            startSportActivityIntent.putExtra("event", event)
            startSportActivityIntent.putExtra("notification_id", notificationID)

            val startSportActivityPendingIntentService = PendingIntent.getService(this, 0, startSportActivityIntent, 0)
            val startSportActivitygAction = android.support.v4.app.NotificationCompat.Action.Builder(
                    R.mipmap.ic_sport,
                    "Commencer activité sportive",
                    startSportActivityPendingIntentService)
                    .build()

            buildedNotification.addAction(startSportActivitygAction)
        }

        return buildedNotification.build()

    }
}