package com.samer.wear.db.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.samer.wear.db.contract.DBContract
import com.samer.wear.db.contract.EventType
import com.samer.wear.db.model.Event
import org.joda.time.DateTime

class EventDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "event.db"

        private const val SQL_CREATE_TABLE =
                "CREATE TABLE " + DBContract.EventEntry.TABLE_NAME + " (" +
                        DBContract.EventEntry.COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DBContract.EventEntry.COLUMN_EVENT_NAME + " TEXT," +
                        DBContract.EventEntry.COLUMN_EVENT_DATE_TIME + " INTEGER ," +
                        DBContract.EventEntry.COLUMN_EVENT_DATE_TIME_TEXT + " TEXT ," +
                        DBContract.EventEntry.COLUMN_EVENT_TYPE + " INTEGER," +
                        DBContract.EventEntry.COLUMN_EVENT_DESCRIPTION + " NUMBER," +
                        DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION + " INTEGER)"

        private const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + DBContract.EventEntry.TABLE_NAME
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(database)
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(database, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertEvent(event: Event): Boolean {
        removeOldEvents()

        val database = writableDatabase

        val values = ContentValues()
        values.put(DBContract.EventEntry.COLUMN_EVENT_NAME, event.name)
        values.put(DBContract.EventEntry.COLUMN_EVENT_DATE_TIME, event.date.millis)
        values.put(DBContract.EventEntry.COLUMN_EVENT_DATE_TIME_TEXT, event.dateText)
        values.put(DBContract.EventEntry.COLUMN_EVENT_TYPE, EventType.getIDFromType(event.type))
        values.put(DBContract.EventEntry.COLUMN_EVENT_DESCRIPTION, event.description)
        values.put(DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION, event.notification)

        database.insert(DBContract.EventEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun updateEvent(event: Event): Boolean {
        removeOldEvents()

        val database = writableDatabase

        val values = ContentValues()
        values.put(DBContract.EventEntry.COLUMN_EVENT_ID, event.id)
        values.put(DBContract.EventEntry.COLUMN_EVENT_NAME, event.name)
        values.put(DBContract.EventEntry.COLUMN_EVENT_DATE_TIME, event.date.millis)
        values.put(DBContract.EventEntry.COLUMN_EVENT_DATE_TIME_TEXT, event.dateText)
        values.put(DBContract.EventEntry.COLUMN_EVENT_TYPE, EventType.getIDFromType(event.type))
        values.put(DBContract.EventEntry.COLUMN_EVENT_DESCRIPTION, event.description)
        values.put(DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION, event.notification)

        database.update(DBContract.EventEntry.TABLE_NAME, values, DBContract.EventEntry.COLUMN_EVENT_ID + " = ?", Array(1) { event.id.toString() })

        return true
    }

    fun deleteEvent(event: Event) {
        val database = writableDatabase

        database.delete(DBContract.EventEntry.TABLE_NAME, DBContract.EventEntry.COLUMN_EVENT_ID + " = ?", Array(1) { event.id.toString() })
    }

    fun readAllEvents(): ArrayList<Event> {
        removeOldEvents()

        val database = writableDatabase

        val cursor: Cursor?
        try {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " ORDER BY " + DBContract.EventEntry.COLUMN_EVENT_DATE_TIME + " ASC", null)
        } catch (sqliteException: SQLiteException) {
            sqliteException.printStackTrace();
            return ArrayList()
        }

        return cursorToList(cursor)
    }

    fun readAllNowEvents(): ArrayList<Event> {
        removeOldEvents()

        val database = writableDatabase

        val cursor: Cursor?
        try {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " ORDER BY " + DBContract.EventEntry.COLUMN_EVENT_DATE_TIME + " ASC", null)
        } catch (sqliteException: SQLiteException) {
            sqliteException.printStackTrace();
            return ArrayList()
        }

        val events = cursorToList(cursor)

        val currentEvents = ArrayList<Event>()

        val currentDateTime = DateTime.now()

        for(event in events)
            if(event.date.year() == currentDateTime.year() &&
                    event.date.monthOfYear() == currentDateTime.monthOfYear() &&
                    event.date.dayOfYear() == currentDateTime.dayOfYear() &&
                    event.date.hourOfDay == currentDateTime.hourOfDay)
            currentEvents.add(event)

        return currentEvents
    }

    fun updateNotification(eventId: Int, notification: Int) {
        val database = writableDatabase

        database.execSQL("UPDATE " + DBContract.EventEntry.TABLE_NAME + " SET " + DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION + " = " + notification + " WHERE " + DBContract.EventEntry.COLUMN_EVENT_ID + " = " + eventId)
    }

    fun readAllEventsByValue(typedText: CharSequence): ArrayList<Event> {
        val database = writableDatabase

        val cursor: Cursor?

        var eventType : Int = EventType.getIdFromTypePrefix(typedText.toString())

        try {
            cursor = database.rawQuery("SELECT * FROM " + DBContract.EventEntry.TABLE_NAME + " WHERE "
                    + DBContract.EventEntry.COLUMN_EVENT_NAME + " LIKE  '" + typedText + "%' OR "
                    + DBContract.EventEntry.COLUMN_EVENT_TYPE + " = " + eventType + " OR "
                    + DBContract.EventEntry.COLUMN_EVENT_DATE_TIME_TEXT + " LIKE  '" + typedText + "%'", null)
        } catch (sqliteException: SQLiteException) {
            sqliteException.printStackTrace();
            return ArrayList()
        }

        return cursorToList(cursor)
    }

    private fun cursorToList(cursor: Cursor): ArrayList<Event> {
        val events = ArrayList<Event>()

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val event = Event()

                event.id = cursor.getInt(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_ID))
                event.name = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_NAME))
                event.date = DateTime(cursor.getLong(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_DATE_TIME)))
                event.dateText = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_DATE_TIME_TEXT))
                event.type = EventType.getTypeFromID(cursor.getInt(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_TYPE)))
                event.description = cursor.getString(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_DESCRIPTION))
                event.notification = cursor.getInt(cursor.getColumnIndex(DBContract.EventEntry.COLUMN_EVENT_NOTIFICATION)) == 1

                events.add(event)

                cursor.moveToNext()
            }
        }
        return events
    }

    private fun removeOldEvents()
    {
        val database = writableDatabase

        database.delete(DBContract.EventEntry.TABLE_NAME, DBContract.EventEntry.COLUMN_EVENT_DATE_TIME + "<?",  arrayOf((DateTime.now().millis - 60000).toString()))
    }

    private fun resetDatabase() {
        val database = writableDatabase

        database.execSQL(SQL_DELETE_TABLE)
        database.execSQL(SQL_CREATE_TABLE)
    }
}