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

package com.abyx.loyalty.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This Task can be used for importing a list of Card-objects into the persistent storage and
 * download there respective logo's.
 *
 * @author Pieter Verschaffelt
 */
public class ImportManager {
    private TaskListener<Void> listener;
    private ThreadPoolExecutor poolExecutor;
    private Context context;
    private int items;
    private int itemsDone;

    public ImportManager(TaskListener<Void> listener, Context context) {
        this.listener = listener;
        this.poolExecutor = new ThreadPoolExecutor(16, 64, 100, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(100));
        this.context = context;
    }

    public void run(Collection<Card> param) {
        items = param.size();

        Database db = new Database(context);
        db.openDatabase();

        for (Card card: param) {
            LogoTask logoTask = new LogoTask(context, new TaskListener<Bitmap>() {
                @Override
                public void onProgressUpdate(double progress) {
                    // No implementation needed
                }

                @Override
                public void onFailed(Throwable exception) {
                    // TODO handle exceptions
                }

                @Override
                public void onDone(Bitmap result) {
                    threadDone();
                }
            });
            logoTask.executeOnExecutor(poolExecutor, card);
            db.addCard(card);
        }

        db.closeDatabase();
    }

    private synchronized void threadDone() {
        itemsDone++;
        if (items == itemsDone) {
            this.listener.onDone(null);
        } else {
            this.listener.onProgressUpdate((double) itemsDone / (double) items);
        }
    }
}
