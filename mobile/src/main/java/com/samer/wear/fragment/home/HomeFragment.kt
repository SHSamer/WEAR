package com.samer.wear.fragment.home

import com.samer.wear.R
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import com.samer.wear.activity.MainActivity
import com.samer.wear.adapter.HomeEventAdapter
import com.samer.wear.db.helper.EventDBHelper

class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var eventDBHelper: EventDBHelper

    private lateinit var homeEventAdapter: HomeEventAdapter

    private lateinit var currentContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")

        currentContext = context!!
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_home, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        eventDBHelper = EventDBHelper(this.context!!)

        val events = eventDBHelper.readAllEvents()

        homeEventAdapter = HomeEventAdapter(currentContext, events, this.activity as MainActivity)

        val eventList = view.findViewById<ListView>(R.id.list_events)
        eventList.adapter = homeEventAdapter

        val inputSearchView = view.findViewById<SearchView>(R.id.input_search)

        inputSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                val events = eventDBHelper.readAllEventsByValue(newText)

                homeEventAdapter = HomeEventAdapter(currentContext, events, activity as MainActivity)

                eventList.adapter = homeEventAdapter
                return true
            }
        })
    }
}
