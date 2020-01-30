package com.samer.wear.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import com.samer.wear.R
import com.samer.wear.db.helper.EventDBHelper
import com.samer.wear.db.model.Event
import com.samer.wear.fragment.creation.EventCreationFragment
import com.samer.wear.fragment.details.EventDetailsFragment
import com.samer.wear.fragment.home.HomeFragment
import com.samer.wear.fragment.voice.VoiceRecognition
import com.samer.wear.service.wear.WearDataService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }


    private lateinit var eventDBHelper: EventDBHelper


    private lateinit var  fragment : Fragment

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_home -> {

                fragment = HomeFragment()

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_create -> {

                fragment = EventCreationFragment()

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_voice_recognition -> {
                fragment = VoiceRecognition()

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_main)

        eventDBHelper = EventDBHelper(this)

        startService(Intent(this, WearDataService::class.java))

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        stopService(Intent(this, WearDataService::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")

        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    fun pushDetailsFragment(event: Event) {
        fragment = EventDetailsFragment.newInstance(event)

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    fun popFragment() {
        supportFragmentManager.popBackStack()
    }
}
