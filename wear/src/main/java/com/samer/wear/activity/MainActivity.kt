package com.samer.wear.activity

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.samer.wear.R
import com.samer.wear.service.MessageListenerService
import com.samer.wear.util.sensor.step.StepDetector
import com.samer.wear.util.sensor.step.StepListener
import java.util.*

class MainActivity : WearableActivity(), SensorEventListener, StepListener {
    companion object {
        const val TAG = "MainActivity"
        private const val WEAR_DATA_PATH = "/wear-data"
    }

    private lateinit var stepTextview: TextView
    private lateinit var startbutton: Button

    private var started: Boolean = false

    private lateinit var timer: Timer
    private lateinit var timerTask: TimerTask
    private lateinit var handler: Handler

    private val DEFAULT_TIME_START_NOTIFICATION = 10L

    private lateinit var mGoogleApiClient: GoogleApiClient

    private lateinit var mNode: Node

    private var stepCounter : Int = 0
    private lateinit var stepDetector : StepDetector
   



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startbutton = findViewById(R.id.button_start)

        stepTextview = findViewById(R.id.text_speed)

        startbutton.setOnClickListener {

            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            if (started) {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    sensorManager.unregisterListener(this)

                    startbutton.text = getString(R.string.start)

                    stepTextview.text = "0"

                    stepCounter = 0

                    stoptimertask()

                    started = false
                }
            } else {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

                    startbutton.text = getString(R.string.stop)

                    startTimer()

                    started = true
                }
            }
            val registerActivity = Intent(applicationContext, Aboutus_activity::class.java)
            startActivity(registerActivity)
            finish()
        }

        startService(Intent(this, MessageListenerService::class.java))

        stepDetector = StepDetector()
        stepDetector.registerListener(this)

        setAmbientEnabled()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2])
        }
    }

    override fun step(timeNs: Long) {
        stepCounter++
        stepTextview.text = stepCounter.toString()
    }

    private fun startTimer() {
        timer = Timer()

        initializeTimerTask()

        timer.schedule(
                timerTask,
                DEFAULT_TIME_START_NOTIFICATION,
                DEFAULT_TIME_START_NOTIFICATION * 300
        )
    }

    private fun stoptimertask() {
        timer.cancel()
    }

    fun initializeTimerTask() {

        handler = Handler()
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    resolveNode(stepTextview.text.toString())
                }
            }
        }
    }

    private fun resolveNode(data: String) {
        Log.d(TAG, "resolveNode")

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build()

        mGoogleApiClient.connect()

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient)
                .setResultCallback { connectedNodes ->
                    for (connectedNode in connectedNodes.nodes) {
                        mNode = connectedNode
                        sendMessage(WEAR_DATA_PATH, data)
                    }
                }
    }

    private fun sendMessage(subject: String, message: String) {
        Log.d(TAG, "sendMessage")

        Wearable.MessageApi.sendMessage(mGoogleApiClient,
                mNode.id,
                subject,
                message.toByteArray())
                .setResultCallback { sendMessageResult ->
                    if (sendMessageResult.status.isSuccess)
                        Log.d(TAG, "Message sended : $message")
                    else
                        Log.e(TAG, "Message not sended")
                }
    }


}
