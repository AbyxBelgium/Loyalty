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

/**
 * This task is responsible for rendering Aurora's that can be used as background for an Activity.
 *
 * @author Pieter Verschaffelt
 */
public class AuroraTask extends AsyncTask<Bitmap, Void, Bitmap> {
    private Context context;
    private TaskListener<Bitmap> listener;
    private Card card;

    public AuroraTask(Context context, TaskListener<Bitmap> listener, Card card) {
        this.context = context;
        this.listener = listener;
        this.card = card;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
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
        }
    }
}
