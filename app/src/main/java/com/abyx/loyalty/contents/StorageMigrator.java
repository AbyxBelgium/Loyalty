package com.abyx.loyalty.contents;

import android.content.Context;

import java.util.ArrayList;

/**
 * This class provides logic for migrating the data saved in a file-based structure to a database-
 * based structure. It smooths the transition from v1.3.1 to v1.4 of this application.
 *
 * @author Pieter Verschaffelt
 */
public class StorageMigrator {
    private Context context;

    public StorageMigrator(Context context) {
        this.context = context;
    }

    /**
     * Convert the file-based storage into the database-based storage.
     */
    public void migrate() {
        IO io = new IO(context);
        ArrayList<Card> cards = io.load();
        Database db = new Database(context);
        db.openDatabase();
        for (Card card: cards) {
            db.addCard(card);
        }
        db.closeDatabase();
        io.clearData();
    }
}
