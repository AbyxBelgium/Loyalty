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
import com.abyx.loyalty.graphics.BarcodeGenerator;
import com.google.zxing.WriterException;

/**
 * Task that renders semi-transparent barcodes.
 *
 * The following exceptions might occur and should be caught by the implementation of the
 * listener's onFailed()-method:
 * <p>
 *     <ul>
 *         <li>com.google.zxing.WriterException</li>
 *     </ul>
 * </p>
 *
 * @author Pieter Verschaffelt
 */
public class BarcodeTask extends AsyncTask<Void, Void, Bitmap> {
    private Context context;
    private TaskListener<Bitmap> listener;
    private Card card;

    private Throwable exception;

    public BarcodeTask(Context context, TaskListener<Bitmap> listener, Card card) {
        this.context = context;
        this.listener = listener;
        this.card = card;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        BarcodeGenerator generator = new BarcodeGenerator(context);

        try {
            return generator.renderBarcode(card.getBarcode(), card.getFormat(), 300, 50);
        } catch (WriterException e) {
            exception = e;
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onProgressUpdate(0.0);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            listener.onProgressUpdate(1.0);
            listener.onDone(bitmap);
        } else if (exception != null) {
            listener.onFailed(exception);
        }
    }
}
