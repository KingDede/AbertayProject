package com.example.jason.groupapp.timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Cécile Journeaux on 12/04/2016.
 * Project created for Abertay University.
 * Contact details: cecile.journeaux@gmail.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /* ==========================================
     *      Singleton implementation
     * ==========================================
     */
    private static DatabaseHelper dbInstance;

    public static DatabaseHelper getInstance( Context context ) {
        if ( dbInstance == null ) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* Initialise constants. */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AbertayDB";
    private static final String EVENTS_TABLE_NAME = "events";
    private static final String[] COLUMN_NAMES = {"dateStart", "dateEnd", "location", "className", "classType", "teacherSurname", "teacherFirstName"};
    /* Construct CREATE query string. */
    private static final String EVENTS_TABLE_CREATE =
            "CREATE TABLE " + EVENTS_TABLE_NAME + " (" +
                    COLUMN_NAMES[0] + " DATE, " +
                    COLUMN_NAMES[1] + " DATE, " +
                    COLUMN_NAMES[2] + " TEXT, " +
                    COLUMN_NAMES[3] + " TEXT, " +
                    COLUMN_NAMES[4] + " TEXT, " +
                    COLUMN_NAMES[5] + " TEXT, " +
                    COLUMN_NAMES[6] + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates the database if it doesn't exist and adds the "contacts" table.
        /* Execute SQL query. */
        db.execSQL(EVENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // can be left empty for the purposes of our tutorial
    }

    public void addEvent(Event e){
        /* Pack contact details in ContentValues object for database insertion. */
        ContentValues row = new ContentValues();
        row.put(this.COLUMN_NAMES[0], Event.getDateToString(e.getDateStart()));
        row.put(this.COLUMN_NAMES[1], Event.getDateToString(e.getDateEnd()));
        row.put(this.COLUMN_NAMES[2], e.getLocation());
        row.put(this.COLUMN_NAMES[3], e.getClassName());
        row.put(this.COLUMN_NAMES[4], e.getClassType());
        row.put(this.COLUMN_NAMES[5], e.getTeacherSurname());
        row.put(this.COLUMN_NAMES[6], e.getTeacherFirstname());
        // The first parameter is a column name, the second is a value.

        /* Get writable database and insert the new row to the "contacts" table. */
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(EVENTS_TABLE_NAME, null, row);
        db.close();
    }

    public int getNumberOfEvents(){
        /* Query the database and check the number of rows returned. */
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(EVENTS_TABLE_NAME, COLUMN_NAMES, null, null, null, null, null, null);

        /* Make sure the query returned a valid result before trying to change text on screen.*/
        if(result != null) {
            /* Display result. */
            int eventsCount = result.getCount();
            db.close();
            return eventsCount;
        } else {
            return -1;
        }
    }

    public ArrayList<Event> getEventsList(){
        /* Get the readable database. */
        SQLiteDatabase db = this.getReadableDatabase();

        /* Get all contacts by querying the database. */
        Cursor result = db.query(EVENTS_TABLE_NAME, COLUMN_NAMES, null, null, null, null, null, null);

        /* Convert results to a list of Contact objects. */
        ArrayList<Event> events = new ArrayList<Event>();

        for(int i = 0; i < result.getCount(); i++){
            result.moveToPosition(i);
            /* Create a Contact object with using data from name, email, phone columns. Add it to list. */
            events.add(new Event(
                Event.getStringToDate(result.getString(0)),
                Event.getStringToDate(result.getString(1)),
                result.getString(2),
                result.getString(3),
                result.getString(4),
                result.getString(5),
                result.getString(6)
            ));
        }

        return events;
    }

    public int removeEvent(Event e){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = this.COLUMN_NAMES[0] + " = '" + Event.getDateToString( e.getDateStart() ) +
                "' AND " + this.COLUMN_NAMES[1] + " = '" + Event.getDateToString( e.getDateEnd() ) +
                "' AND " + this.COLUMN_NAMES[2] + " = '" + e.getLocation() +
                "' AND " + this.COLUMN_NAMES[3] + " = '" + e.getClassName() +
                "' AND " + this.COLUMN_NAMES[4] + " = '" + e.getClassType() +
                "' AND " + this.COLUMN_NAMES[5] + " = '" + e.getTeacherSurname() +
                "' AND " + this.COLUMN_NAMES[6] + " = '" + e.getTeacherFirstname();
        // Returns the number of affected rows. 0 means no rows were deleted.
        System.out.println(e.getDateStart().toString());
        return db.delete(EVENTS_TABLE_NAME, whereClause, null);
    }

    public Event getNextEvent(String date) {

        // Get the readable database.
        SQLiteDatabase db = this.getReadableDatabase();

        // Get all events by querying the database.
        String where = COLUMN_NAMES[0] + " > ?";
        String[] whereParams = { date };
        String orderBy = COLUMN_NAMES[0] + " ASC";
        String limit = "1";
        Cursor result = db.query(EVENTS_TABLE_NAME, COLUMN_NAMES, where, whereParams, null, null, orderBy, limit);

        // Convert results to an Event object.
        Event nextEvent = null;

        if ( result != null && result.getCount() == 1 && result.moveToFirst() ) {
            nextEvent = new Event(
                    Event.getStringToDate(result.getString(0)),
                    Event.getStringToDate(result.getString(1)),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getString(6)
            );
        }
        db.close();

        return nextEvent;
    }
}
