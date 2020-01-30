package com.samer.wear.db.contract

object EventType {

    val eventTypes = arrayOf("Sport", "Meeting", "Raiding")

    @JvmStatic
    fun getTypeFromID(id : Int) : String
    {
        return eventTypes[id]
    }

    @JvmStatic
    fun getIDFromType(eventType : String) : Int
    {
        for(index in 0 until eventTypes.size)
            if(eventType.toLowerCase() == eventTypes[index].toLowerCase())
                return index

        return -1
    }

    @JvmStatic
    fun getIdFromTypePrefix(eventType : String) : Int
    {
        for(index in 0 until eventTypes.size)
            if(eventType.substring(0, eventType.length).toLowerCase() == eventTypes[index].substring(0, eventType.length).toLowerCase())
                return index

        return -1
    }
}