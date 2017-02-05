package com.abyx.loyalty.contents;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database class that manages all persistent data.
 *
 * @author Pieter Verschaffelt
 */
public class Database extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    // The following strings are all used for creating or upgrading the database
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.TABLE_CARD + " (" +
            DatabaseContract.COLUMN_ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.COLUMN_NAME + " TEXT," +
            DatabaseContract.COLUMN_BARCODE + " TEXT," +
            DatabaseContract.COLUMN_BARCODE_FORMAT + " TEXT," +
            DatabaseContract.COLUMN_IMAGE_URL + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.TABLE_CARD;

    public Database(Context context) {
        super(context, "LOYALTY_DB", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override 
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
