package com.kazimir.pedometer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler databaseHandler;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "USER_DATA";
    private static final String TABLE_STEPS = "STEPS";
    private static final String KEY_DATE = "DATE";
    private static final String KEY_COUNT = "COUNT";


    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler(context.getApplicationContext());
        }
        return databaseHandler;
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_STEPS +
                        "(" + KEY_DATE + " STRING PRIMARY KEY,"
                        + KEY_COUNT + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);

        // Create tables again
        onCreate(db);
    }

    void addDayData(DayData dayData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, dayData.getDate());
        values.put(KEY_COUNT, dayData.getSteps());

        db.insert(TABLE_STEPS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close();
    }

    DayData getDayData(String date) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_STEPS, new String[]{KEY_DATE,
                        KEY_COUNT}, KEY_DATE + "=?",
                new String[]{date}, null, null, null, null);

        int cursor_count = cursor.getCount();
        if (cursor_count == 0) {
            return null;
        }

        cursor.moveToFirst();

        return new DayData(cursor.getString(0), Integer.parseInt(cursor.getString(1)));
    }

    int getStepCountForDay(String date) {

        DayData dayData = getDayData(date);
        if (dayData != null) {
            return dayData.getSteps();
        } else {
            return 0;
        }

    }

    public int updateDayData(DayData dayData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, dayData.getSteps());

        return db.update(TABLE_STEPS, values, KEY_DATE + " = ?",
                new String[]{dayData.getDate()});
    }

    public void updateDayDataOrCreate(DayData newDayData) {
        String date = newDayData.getDate();
        DayData dayData = getDayData(date);

        if (dayData != null) {
            updateDayData(newDayData);
        } else {
            addDayData(newDayData);
        }
    }
}