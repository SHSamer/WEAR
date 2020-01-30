package com.samer.wear.fragment.voice

import android.support.v4.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.samer.wear.R
import com.samer.wear.R.layout.fragment_voice_recognition
import com.samer.wear.fragment.creation.EventCreationFragment
import com.samer.wear.fragment.home.HomeFragment


class VoiceRecognition: Fragment() {


    companion object {
      private const val TAG = "VoiceRecoFragment"
  }

    private lateinit var speakButton: Button
    private lateinit var mText: TextView
    private lateinit var recognizer: SpeechRecognizer
    private var mAnswer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragment_voice_recognition, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        speakButton = view.findViewById<Button>(R.id.speakButton)
        mText = view.findViewById<TextView>(R.id.voiceText)

        speakButton.setOnClickListener { speakButtonClicked() }
        recognizer = SpeechRecognizer.createSpeechRecognizer(activity)

        verifyRecognitionService()

    }

    fun verifyRecognitionService(){
        val pm: PackageManager = activity?.packageManager!!
        Log.d("PackageManager : ",pm.toString())

        val activities = pm.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0)
        if(activities.size == 0) {
            speakButton.isEnabled = false
            speakButton.text = "Recognizer not present"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

        for(matche in matches!!){

            Log.d("match : ", matche)
            val words = matche.split(" ")

            if(words.contains("creation") || words.contains("create")){
                mAnswer = "creation"
                break
            }
            else if(words.contains("home") || words.contains("d\'home")){
                mAnswer = "home"
                break
            }
            else {
                mAnswer = matche
            }
        }

        mText.text = mAnswer

        if(mAnswer == "creation" || mAnswer == "home"){

            var fragment = Fragment()
            Log.d("mAnswer : ", mAnswer)

            if(mAnswer == "creation"){
                fragment = EventCreationFragment()
                Log.d("Fragment ", fragment.toString())
            }
            else if (mAnswer == "home"){
                fragment = HomeFragment()
                Log.d("Fragment ", fragment.toString())
            }

            val fragmentManager = activity?.supportFragmentManager

            val fragmentTransaction = fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container,fragment)
                    ?.commit()

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun speakButtonClicked(){
        Log.d("DEBUG :" , "Button Clicked !")
        startVoiceRecognitionActivity()
    }

    private fun startVoiceRecognitionActivity() {
        val intent  = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test")

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        startActivityForResult(intent, 1234)
    }
}