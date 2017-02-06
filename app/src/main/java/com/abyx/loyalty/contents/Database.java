package com.abyx.loyalty.contents;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.abyx.loyalty.exceptions.InvalidCardException;
import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages all the persistent data access for this application. All changes to objects
 * have to be propagated into the database immediate.
 *
 * @author Pieter Verschaffelt
 */
public class Database {
    private DatabaseHelper helper;
    private SQLiteDatabase database;

    public Database(Context context) {
        helper = new DatabaseHelper(context);
        database = helper.getWritableDatabase();
    }

    /**
     * Add a new card to the database. The given Card-object will be saved persistently in the
     * database. The ID-parameter of this card will be filled in by this method.
     *
     * @param card The card that should be added to the database.
     */
    public void addCard(Card card) {
        ContentValues toAdd = generateCardContentValues(card);
        long newID = database.insert(DatabaseContract.TABLE_CARD, null, toAdd);
        card.setID(newID);
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
        temp.put(DatabaseContract.COLUMN_IMAGE_URL, card.getImageLocation());
        return temp;
    }

    /**
     * All changes to the given Card-object will be saved persistently in the database. The Card-
     * object is uniquely identified by it's ID (Must be bigger than 0).
     *
     * @param card The card that should be updated.
     * @throws InvalidCardException Whenever a card is being updated with an invalid ID (-1).
     */
    public void updateCard(Card card) throws InvalidCardException {
        if (card.getID() <= 0) {
            throw new InvalidCardException("Card has invalid id: " + card.getName());
        }

        ContentValues toUpdate = generateCardContentValues(card);

        String selection = DatabaseContract.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(card.getID())};

        database.update(DatabaseContract.TABLE_CARD, toUpdate, selection, selectionArgs);
    }

    /**
     * Remove the given card from the persistent storage. This object should not be used afterwards.
     * This Card-object is uniquely identified by it's ID (Must be bigger than 0).
     *
     * @param card The card that should be removed from the persistent storage.
     * @throws InvalidCardException Whenever a card is being deleted with an invalid ID (-1).
     */
    public void deleteCard(Card card) throws InvalidCardException {
        if (card.getID() <= 0) {
            throw new InvalidCardException("Card has invalid id: " + card.getName());
        }
    }

    /**
     * This function returns all loyalty cards that are stored by this application.
     *
     * @return All loyalty cards that are stored in this application by the current user.
     */
    public List<Card> getAllCards() {
        String[] projection = {
                DatabaseContract.COLUMN_ID,
                DatabaseContract.COLUMN_NAME,
                DatabaseContract.COLUMN_BARCODE,
                DatabaseContract.COLUMN_BARCODE_FORMAT,
                DatabaseContract.COLUMN_IMAGE_URL
        };

        Cursor cursor = database.query(DatabaseContract.TABLE_CARD, projection, null, null, null, null, null);

        List<Card> cards = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_NAME));
                String barcode = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_BARCODE));
                String barcodeFormat = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_BARCODE_FORMAT));
                String imageURL = cursor.getString(cursor.getColumnIndex(DatabaseContract.COLUMN_IMAGE_URL));

                BarcodeFormat format = BarcodeFormat.valueOf(barcodeFormat);

                Card card = new Card(name, barcode, imageURL, format);
                card.setID(id);
                cards.add(card);
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return cards;
    }
}