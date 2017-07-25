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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Display;
import android.view.WindowManager;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import be.abyx.aurora.aurora.AuroraFactory;
import be.abyx.aurora.aurora.BlurryAurora;
import be.abyx.aurora.aurora.ParallelAuroraFactory;

/**
 * This task is responsible for rendering Aurora's that can be used as background for an Activity.
 *
 * The following exceptions might occur and should be caught by the implementation of the
 * listener's onFailed()-method:
 * <p>
 *     <ul>
 *         <li>FileNotFoundException</li>
 *         <li>IOException</li>
 *     </ul>
 * </p>
 *
 * @author Pieter Verschaffelt
 */
public class AuroraTask extends AsyncTask<Bitmap, Void, Bitmap> {
    private Context context;
    private TaskListener<Bitmap> listener;
    private Card card;
    private Throwable exception;


    public AuroraTask(Context context, TaskListener<Bitmap> listener, Card card) {
        this.context = context;
        this.listener = listener;
        this.card = card;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        String auroraFileName = Constants.CACHE_DIR_AURORA + Integer.toString(card.getName().hashCode()) + "." + Constants.IMAGE_FORMAT;

        Bitmap output;

        File file = context.getFileStreamPath(auroraFileName);
        if (file == null || !file.exists()) {
            AuroraFactory factory = new ParallelAuroraFactory(context);
            // Get device resolution
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            output = factory.createAuroraBasedUponDrawable(params[0], new BlurryAurora(context), size.x, size.y);

            // Write Bitmap to internal storage for caching purposes
            try {
                FileOutputStream fos = context.openFileOutput(auroraFileName, Context.MODE_PRIVATE);
                output.compress(Constants.IMAGE_COMPRESS_FORMAT, Constants.IMAGE_QUALITY, fos);
                return output;
            } catch (IOException e) {
                exception = e;
                return null;
            }
        } else {
            try (FileInputStream in = context.openFileInput(auroraFileName)) {
                return BitmapFactory.decodeStream(in);
            } catch (Throwable e) {
                exception = e;
                return null;
            }
        }
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
        } else {
            listener.onFailed(exception);
        }
    }
}
