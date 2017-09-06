/*
 * Copyright 2017 Abyx (https://abyx.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abyx.loyalty.contents;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper class that manages the creation and connections to a database.
 *
 * @author Pieter Verschaffelt
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;

    // The following strings are all used for creating or upgrading the database
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.TABLE_CARD + " (" +
            DatabaseContract.COLUMN_ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.COLUMN_NAME + " TEXT," +
            DatabaseContract.COLUMN_BARCODE + " TEXT," +
            DatabaseContract.COLUMN_BARCODE_FORMAT + " TEXT," +
            DatabaseContract.COLUMN_LAST_SEARCHED + " INTEGER DEFAULT 0," +
            DatabaseContract.COLUMN_IMAGE_URL + " TEXT," +
            DatabaseContract.COLUMN_HIT_COUNT + " INTEGER DEFAULT 0)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.TABLE_CARD;

    private static final String SQL_UPDATE_V1_TO_V2 =
            "ALTER TABLE " + DatabaseContract.TABLE_CARD + " ADD COLUMN " + DatabaseContract.COLUMN_LAST_SEARCHED + " INTEGER DEFAULT 0";

    private static final String SQL_UPDATE_V2_TO_V3 =
            "ALTER TABLE " + DatabaseContract.TABLE_CARD + " ADD COLUMN " + DatabaseContract.COLUMN_HIT_COUNT + " INTEGER DEFAULT 0";

    public DatabaseHelper(Context context) {
        super(context, "LOYALTY_DB", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(SQL_UPDATE_V1_TO_V2);
        } else if (oldVersion == 2 && newVersion == 3) {
            db.execSQL(SQL_UPDATE_V2_TO_V3);
        } else {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}