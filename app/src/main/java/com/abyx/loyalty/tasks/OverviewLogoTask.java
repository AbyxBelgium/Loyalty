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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.graphics.ThumbnailGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import be.abyx.aurora.ImageUtils;

/**
 * This Task generates all small thumbnails for store logos that are shown in an overview or summary
 * list. One of the parameters for this Task is an ImageView. This is needed as an Adapter always
 * has to return the View immediately and is not able to wait for an async thread to complete.
 *
 * The following exceptions might occur and should be caught by the implementation of the
 * listener's onFailed()-method:
 * <p>
 *     <ul>
 *         <li>IOException</li>
 *     </ul>
 * </p>
 *
 * @author Pieter Verschaffelt
 */
public class OverviewLogoTask extends AsyncTask<Bitmap, Void, Bitmap> {
    private Context context;
    private TaskListener<Bitmap> listener;
    private Card card;

    /**
     * @param context A valid ApplicationContext.
     * @param listener A listener that should respond to state changes of this task.
     * @param card The loyalty Card for which a new thumbnail should be generated.
     */
    public OverviewLogoTask(Context context, TaskListener<Bitmap> listener, Card card) {
        this.context = context;
        this.listener = listener;
        this.card = card;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        String logoFileName = Constants.CACHE_DIR_LOGO_OVERVIEW + Integer.toString(card.getName().hashCode()) + "." + Constants.IMAGE_FORMAT;

        File file = context.getFileStreamPath(logoFileName);

        Bitmap output;

        if (file == null || !file.exists()) {
            Bitmap logo = params[0];
            ThumbnailGenerator generator = new ThumbnailGenerator(this.context);
            output = generator.generateThumbnail(logo);

            // Save file in persistent storage
            try (FileOutputStream fos = context.openFileOutput(logoFileName, Context.MODE_PRIVATE)) {
                output.setHasAlpha(true);
                output.compress(Constants.IMAGE_COMPRESS_FORMAT, Constants.IMAGE_QUALITY, fos);
            } catch (IOException e) {
                this.listener.onFailed(e);
                return null;
            }
        } else {
            try (FileInputStream in = context.openFileInput(logoFileName)) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                output = BitmapFactory.decodeStream(in, null, opts);
            } catch (IOException e) {
                this.listener.onFailed(e);
                return null;
            }
        }

        return output;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.listener.onProgressUpdate(0.0);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            this.listener.onProgressUpdate(1.0);
            this.listener.onDone(bitmap);
        }
    }
}
