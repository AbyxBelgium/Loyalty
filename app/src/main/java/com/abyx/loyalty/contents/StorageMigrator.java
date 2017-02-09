/**
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
