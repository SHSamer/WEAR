package com.samer.wear.service.wear

import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

class WearDataService : WearableListenerService() {
    private lateinit var googleApiClient: GoogleApiClient

    companion object {
        private const val TAG = "WearDataService"
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
        Log.d(TAG, "onDestroy")

        googleApiClient.disconnect()

        super.onDestroy()
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        Log.d(TAG, "onMessageReceived")

        val messageContent = String(messageEvent!!.data)

        Log.d(TAG, messageContent)
    }
}