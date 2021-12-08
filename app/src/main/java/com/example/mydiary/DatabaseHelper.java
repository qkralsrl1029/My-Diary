package com.example.mydiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Diary";
    public static final String TABLE_NAME = "record";
    //columns
    public static final String PRIMARY_KEY= "date";
    public static final String POSITION_X="longitude";
    public static final String POSITION_Y="latitude";
    public static final String IS_HAPPY="smile";
    public static final String IS_SAD="sad";
    public static final String IS_BORING="boring";
    public static final String IS_SURPRISED="surprised";
    public static final String IS_LOVED="loved";
    public static final String BODY="body";

    //for internal DB
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    PRIMARY_KEY + " CHAR(20) PRIMARY KEY," +
                    POSITION_X + " REAL," +
                    POSITION_Y + " REAL," +
                    IS_HAPPY + " INTEGER," +
                    IS_SAD + " INTEGER," +
                    IS_BORING + " INTEGER," +
                    IS_SURPRISED + " INTEGER," +
                    IS_LOVED + " INTEGER," +
                    BODY + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
