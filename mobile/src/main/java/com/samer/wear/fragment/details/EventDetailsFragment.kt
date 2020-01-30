package com.samer.wear.fragment.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.samer.wear.activity.MainActivity
import com.samer.wear.db.contract.EventType
import com.samer.wear.db.helper.EventDBHelper
import com.samer.wear.db.model.Event
import com.samer.wear.R

class EventDetailsFragment  : Fragment(), AdapterView.OnItemSelectedListener
{
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    companion object
    {
        private const val TAG = "EventDetailsFragment"

        fun newInstance(event : Event): EventDetailsFragment {
            val fragment = EventDetailsFragment()
            val args = Bundle()
            args.putSerializable("event", event)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var eventDBHelper : EventDBHelper

    private lateinit var spinnerType: Spinner

    private lateinit var event : Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        event = this.arguments!!["event"] as Event

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        eventDBHelper = EventDBHelper(this.context!!)

        val eventDetailsNameEditText = view.findViewById<EditText>(R.id.eventDetailsName)

        eventDetailsNameEditText.text = Editable.Factory.getInstance().newEditable(event.name)

        eventDetailsNameEditText.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT){
                view.findViewById<ImageView>(R.id.eventDetailsNameError).visibility = View.INVISIBLE
                false
            } else {
                true
            }
        }

        spinnerType = view.findViewById(R.id.eventDetailsType)

        spinnerType.onItemSelectedListener = this

        val aa = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, EventType.eventTypes)

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerType.adapter = aa

        val eventDetailsDescriptionEditText = view.findViewById<EditText>(R.id.eventDetailsDescription)

        eventDetailsDescriptionEditText.text = Editable.Factory.getInstance().newEditable(event.description)

        val eventDetailsModify = view.findViewById<Button>(R.id.eventDetailsModify)

        eventDetailsModify.setOnClickListener {

            var validEvent = true

            if (eventDetailsNameEditText.text.isEmpty()) {
                Toast.makeText(this.context,
                        "Invalid title", Toast.LENGTH_LONG).show()

                view.findViewById<ImageView>(R.id.eventDetailsNameError).visibility = View.VISIBLE

                validEvent = false
            }


            if (validEvent) {
                event.name = eventDetailsNameEditText.text.toString()
                event.type = spinnerType.selectedItem.toString()
                event.description = eventDetailsDescriptionEditText.text.toString()

                eventDBHelper.updateEvent(event)

                Toast.makeText(this.context,
                        "Event modified", Toast.LENGTH_LONG).show()

                (this.activity as MainActivity).popFragment()
            }
        }

        val eventDetailsDeleteButton = view.findViewById<Button>(R.id.eventDetailsDelete)

        eventDetailsDeleteButton.setOnClickListener {
            eventDBHelper.deleteEvent(event)

            Toast.makeText(this.context,
                    "Event deleted", Toast.LENGTH_LONG).show()

            (this.activity as MainActivity).popFragment()
        }
    }
}
