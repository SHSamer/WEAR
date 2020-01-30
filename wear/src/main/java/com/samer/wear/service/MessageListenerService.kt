package com.samer.wear.service

import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.samer.wear.activity.MainActivity

class MessageListenerService : WearableListenerService() {

    private lateinit var googleApiClient: GoogleApiClient

    companion object {
        private const val TAG = "MessageListenerService"
        private const val START_ACTIVITY_PATH = "/start-activity"
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build()

        googleApiClient.connect()
    }

    override fun onDestroy() {
        googleApiClient.disconnect()
        super.onDestroy()
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        Log.d(TAG, "onMessageReceived")

        if (messageEvent!!.path == START_ACTIVITY_PATH) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}