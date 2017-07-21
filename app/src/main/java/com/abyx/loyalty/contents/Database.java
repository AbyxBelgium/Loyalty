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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.abyx.loyalty.exceptions.DatabaseNotOpenException;
import com.abyx.loyalty.exceptions.InvalidCardException;
import com.abyx.loyalty.managers.ChangeObservable;
import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages all the persistent data access for this application. All changes to objects
 * have to be propagated into the database immediate.
 *
 * @author Pieter Verschaffelt
 */
public class Database extends ChangeObservable<List<Card>> {
    private DatabaseHelper helper;
    private SQLiteDatabase database;

    private List<Card> cards;

    // Default sort order is ascending...
    private String orderBy = DatabaseContract.COLUMN_NAME + " ASC";

    public Database(Context context) {
        helper = new DatabaseHelper(context);
    }

    public void openDatabase() {
        database = helper.getWritableDatabase();
    }

    /**
     * Add a new card to the database. The given Card-object will be saved persistently in the
     * database. The ID-parameter of this card will be filled in by this method.
     *
     * @param card The card that should be added to the database.
     */
    public void addCard(Card card) {
        if (database == null) {
            throw new DatabaseNotOpenException("Database is not open!");
        }

        ContentValues toAdd = generateCardContentValues(card);
        long newID = database.insert(DatabaseContract.TABLE_CARD, null, toAdd);
        card.setID(newID);

        this.getAllCards();
    }

    /**
     * Converts the given Card-object to a ContentValues-object that can be used to update or add
     * the card to the database.
     *
     * @param card The Card-object whose values should be used to populate the ContentValues-object.
     * @return A ContentValues-object containing the correct values and columns for the given card.
     */
    private ContentValues generateCardContentValues(Card card) {
        ContentValues temp = new ContentValues();
        temp.put(DatabaseContract.COLUMN_NAME, card.getName());
        temp.put(DatabaseContract.COLUMN_BARCODE, card.getBarcode());
        temp.put(DatabaseContract.COLUMN_BARCODE_FORMAT, card.getFormat().toString());
        temp.put(DatabaseContract.COLUMN_IMAGE_URL, card.getImageURL());
        temp.put(DatabaseContract.COLUMN_LAST_SEARCHED, card.getLastSearched());
        return temp;
    }

    /**
     * All changes to the given Card-object will be saved persistently in the database. The Card-
     * object is uniquely identified by it's ID (Must be bigger than 0).
     *
     * @param card The card that should be updated.
     * @throws InvalidCardException Whenever a card is being updated with an invalid ID (-1).
     * @throws DatabaseNotOpenException Whenever this method is used before calling openDatabase()
     */
    public void updateCard(Card card) {
        if (database == null) {
            throw new DatabaseNotOpenException("Database is not open!");
        }

        if (card.getID() <= 0) {
            throw new InvalidCardException("Card has invalid id: " + card.getName());
        }

        ContentValues toUpdate = generateCardContentValues(card);

        String selection = DatabaseContract.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(card.getID())};

        database.update(DatabaseContract.TABLE_CARD, toUpdate, selection, selectionArgs);
        this.getAllCards();
    }

    /**
     * Remove the given card from the persistent storage. This object should not be used afterwards.
     * This Card-object is uniquely identified by it's ID (Must be bigger than 0).
     *
     * @param card The card that should be removed from the persistent storage.
     * @throws InvalidCardException Whenever a card is being deleted with an invalid ID (-1).
     * @throws DatabaseNotOpenException Whenever this method is used before calling openDatabase()
     */
    public void deleteCard(Card card) {
        if (database == null) {
            throw new DatabaseNotOpenException("Database is not open!");
        }

        if (card.getID() <= 0) {
            throw new InvalidCardException("Card has invalid id: " + card.getName());
        }

        String selection = DatabaseContract.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(card.getID())};

        database.delete(DatabaseContract.TABLE_CARD, selection, selectionArgs);
        this.getAllCards();
    }

    /**
     * Returns the Card-object associated with the given database ID.
     *
     * @param id Valid database id from whom a new Card-object should be created.
     * @return A new Card-object associated with this ID.
     */
    public Card getCardByID(long id) {
        if (database == null) {
            throw new DatabaseNotOpenException("Database is not open!");
        }

        String[] projection = {
                DatabaseContract.COLUMN_ID,
                DatabaseContract.COLUMN_NAME,
                DatabaseContract.COLUMN_BARCODE,
                DatabaseContract.COLUMN_BARCODE_FORMAT,
                DatabaseContract.COLUMN_IMAGE_URL
        };

        String selection = DatabaseContract.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(DatabaseContract.TABLE_CARD, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long cardID = cursor.getLong(cursor.getColumnIndex(DatabaseContract.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_NAME));
            String barcode = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_BARCODE));
            String barcodeFormat = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_BARCODE_FORMAT));
            String imageURL = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_IMAGE_URL));

            int lastSearched;
            if (cursor.getColumnIndex(DatabaseContract.COLUMN_LAST_SEARCHED) == 0) {
                lastSearched = cursor.getInt(cursor.getColumnIndex(DatabaseContract.COLUMN_LAST_SEARCHED));
            } else {
                lastSearched = 0;
            }

            Card output = new Card(name, barcode, imageURL, BarcodeFormat.valueOf(barcodeFormat), lastSearched);
            output.setID(cardID);
            cursor.close();
            return output;
        } else {
            cursor.close();
            return null;
        }
    }

    public void getAllCardsSorted(boolean ascending) {
        if (ascending) {
            this.orderBy = DatabaseContract.COLUMN_NAME + " ASC";
        } else {
            this.orderBy = DatabaseContract.COLUMN_NAME + " DESC";
        }
        this.getAllCards();
    }

    /**
     * This method receives all cards from the database and sends the queried data to all listeners.
     * NOTE: Database must be open before invoking this function.
     */
    public void getAllCards() {
        if (database == null) {
            throw new DatabaseNotOpenException("Database is not open!");
        }

        String[] projection = {
                DatabaseContract.COLUMN_ID,
                DatabaseContract.COLUMN_NAME,
                DatabaseContract.COLUMN_BARCODE,
                DatabaseContract.COLUMN_BARCODE_FORMAT,
                DatabaseContract.COLUMN_IMAGE_URL
        };

        Cursor cursor = database.query(DatabaseContract.TABLE_CARD, projection, null, null, null, null, this.orderBy);

        cards = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_NAME));
                String barcode = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_BARCODE));
                String barcodeFormat = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_BARCODE_FORMAT));
                String imageURL = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_IMAGE_URL));

                int lastSearched;
                if (cursor.getColumnIndex(DatabaseContract.COLUMN_LAST_SEARCHED) == 0) {
                    lastSearched = cursor.getInt(cursor.getColumnIndex(DatabaseContract.COLUMN_LAST_SEARCHED));
                } else {
                    lastSearched = 0;
                }

                BarcodeFormat format = BarcodeFormat.valueOf(barcodeFormat);

                Card card = new Card(name, barcode, imageURL, format, lastSearched);
                card.setID(id);
                cards.add(card);

                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        this.notifyListeners(cards);
    }

    public void closeDatabase() {
        database.close();
        helper.close();
        database = null;
    }
}
