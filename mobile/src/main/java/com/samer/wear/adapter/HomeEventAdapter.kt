package com.samer.wear.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.samer.wear.activity.MainActivity
import com.samer.wear.db.helper.EventDBHelper
import com.samer.wear.db.model.Event
import com.samer.wear.R

class HomeEventAdapter internal constructor(private val mycontext: Context, private val list: List<Event>, private val activity : MainActivity) : ArrayAdapter<Event>(mycontext, R.layout.event_row, list) {

    private var eventDBHelper = EventDBHelper(this.context)

    override fun getCount(): Int
    {
        return list.size
    }

    override fun getItem(position: Int): Event
    {
        return list[position]
    }

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }

    private fun isChecked(position: Int): Boolean
    {
        return list[position].notification
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        eventDBHelper = EventDBHelper(context)

        var rowView = convertView

        var viewHolder = ViewHolder()
        if (rowView == null) {
            val inflater = (context as Activity).layoutInflater
            rowView = inflater.inflate(R.layout.event_row, null)

            viewHolder.activeNotification = rowView.findViewById(R.id.rowEventCheckBox) as CheckBox
            viewHolder.eventName = rowView.findViewById(R.id.rowEventName) as TextView
            viewHolder.eventType = rowView.findViewById(R.id.rowEventType) as TextView
            viewHolder.eventDate = rowView.findViewById(R.id.rowEventDate) as TextView

            rowView.tag = viewHolder

        } else {
            viewHolder = rowView.tag as ViewHolder
        }

        viewHolder.activeNotification!!.isChecked = !list[position].notification

        val event = list[position]
        viewHolder.eventName!!.text = event.name
        viewHolder.eventType!!.text = event.type
        viewHolder.eventDate!!.text = event.dateText

        viewHolder.activeNotification!!.tag = position

        viewHolder.eventName!!.setOnClickListener{
            val eventModel = list[position]

            activity.pushDetailsFragment(eventModel)
        }

        viewHolder.eventType!!.setOnClickListener{
            val eventModel = list[position]

            activity.pushDetailsFragment(eventModel)
        }

        viewHolder.eventDate!!.setOnClickListener{
            val eventModel = list[position]

            activity.pushDetailsFragment(eventModel)
        }

        viewHolder.activeNotification!!.setOnClickListener {
            val newState = !list[position].notification
            list[position].notification = newState

            val eventModel = list[position]

            var notifictionActivated = 0

            if(eventModel.notification)
                notifictionActivated = 1

            eventDBHelper.updateNotification(eventModel.id, notifictionActivated)
        }

        viewHolder.activeNotification!!.isChecked = isChecked(position)

        return rowView!!
    }
}