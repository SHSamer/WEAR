package com.samer.wear.db.contract

import android.provider.BaseColumns

object DBContract {
    class EventEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "events"
            const val COLUMN_EVENT_ID = "event_id"
            const val COLUMN_EVENT_NAME = "event_name"
            const val COLUMN_EVENT_DATE_TIME = "event_date_time"
            const val COLUMN_EVENT_DATE_TIME_TEXT = "event_date_time_text"
            const val COLUMN_EVENT_TYPE = "event_type"
            const val COLUMN_EVENT_DESCRIPTION = "event_description"
            const val COLUMN_EVENT_NOTIFICATION = "event_notification"
        }
    }
}