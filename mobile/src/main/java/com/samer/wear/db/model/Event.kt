package com.samer.wear.db.model

import com.samer.wear.db.util.DateUtils
import org.joda.time.DateTime
import java.io.Serializable

class Event : Serializable
{
    var id: Int = 0
    var name: String = ""
    var date: DateTime = DateTime.now()
    var dateText : String = ""
    var type: String = ""
    var description: String = ""
    var notification: Boolean = false

    override fun toString(): String {
        val eventToString = StringBuilder()

        eventToString.append(name)
        eventToString.append(" - ")
        eventToString.append(DateUtils.dateTimeWithTimeToString(date))
        eventToString.append(" - ")
        eventToString.append(type)

        if(!description.isEmpty())
        eventToString.append(" - ")
        eventToString.append(description)

        return eventToString.toString()
    }
}