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
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.extra.Constants;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import be.abyx.aurora.CropUtility;
import be.abyx.aurora.ParallelShapeFactory;
import be.abyx.aurora.RectangleShape;
import be.abyx.aurora.ResizeUtility;

/**
 * This task is used for asynchronously looking up and downloading a new logo from the Loyalty API.
 * It is also used for retrieving a previously stored logo from the internal storage.
 *
 * The following exceptions might occur and should be caught by the implementation of the
 * listener's onFailed()-method:
 * <p>
 *     <ul>
 *         <li>MalformedURLException</li>
 *         <li>FileNotFoundException</li>
 *         <li>IOException</li>
 *     </ul>
 * </p>
 *
 * @author Pieter Verschaffelt
 */
public class LogoTask extends AsyncTask<Card, Void, Bitmap> {
    private Context context;
    private TaskListener<Bitmap> listener;

    public LogoTask(Context context, TaskListener<Bitmap> listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * This method will first request the correct URL for a logo when no URL has been set for the
     * given card. When a URL has already been set, it will look and see if the logo has already
     * been saved on the internal storage of the device. If that's the case, the already existing
     * logo will be reloaded and returned as a Bitmap. If the logo is not present on the internal
     * storage, it will be downloaded and stored in the persistent storage for future reference.
     *
     * The hashcode of the given card's storename is used as a unique identifier for the cards
     * logo stored in persistent storage. When no file with this hashcode as a filename is found, it
     * is regarded as "non-existent" and will be redownloaded.
     *
     * @param params An array of length 1 with a valid Card-object in it. The Card's store name will
     *               be used for looking up the correct logo.
     * @return A Bitmap that represents the given Card's logo.
     */
    @Override
    @Nullable
    protected Bitmap doInBackground(Card... params) {
        Card card = params[0];

        String logoFileName = Constants.CACHE_DIR_LOGO_RAW + Integer.toString(card.getName().hashCode()) + "." + Constants.IMAGE_FORMAT;

        Bitmap output;

        if (card.getImageURL().equals("")) {
            // Look for a logo for this store
            ApiConnector connector = new ApiConnector();
            try {
                String logo = connector.getStoreLogo(card.getName());
                card.setImageURL(logo);

                // Make new image url persistent
                Database db = new Database(this.context);
                db.openDatabase();
                db.updateCard(card);
                db.closeDatabase();

                output = downloadLogo(card.getImageURL(), logoFileName);
            } catch (IOException | JSONException e) {
                this.listener.onFailed(new IOException(e));
                return null;
            }
        }  else {
            // Check if the logo is stored in the persistent storage of this device or not.
            File file = context.getFileStreamPath(logoFileName);
            if (file == null || !file.exists()) {
                // We have to redownload the logo
                output = downloadLogo(card.getImageURL(), logoFileName);
            } else {
                // File exists, we should reload it.
                try (FileInputStream in = context.openFileInput(logoFileName)) {
                    output = BitmapFactory.decodeStream(in);
                } catch (Throwable e) {
                    this.listener.onFailed(e);
                    return null;
                }
            }
        }

        return output;
    }

    private Bitmap downloadLogo(String url, String logoFileName) {
        try (InputStream in = new java.net.URL(url).openStream()) {
            Bitmap output = BitmapFactory.decodeStream(in);

            CropUtility cropUtility = new CropUtility();
            Bitmap cropped = cropUtility.rectangularCrop(output, Color.WHITE, Constants.MAGIC_CROP_TOLERANCE);

            ResizeUtility resizeUtility = new ResizeUtility();
            Bitmap resized = resizeUtility.resizeAndSquare(cropped, 768, 0);

            ParallelShapeFactory parallelShapeFactory = new ParallelShapeFactory();
            Bitmap out = parallelShapeFactory.createShape(new RectangleShape(context), resized, Color.WHITE, 15);

            Bitmap magicCropped = cropUtility.magicCrop(out, Color.WHITE, Constants.MAGIC_CROP_TOLERANCE);

            FileOutputStream fos = context.openFileOutput(logoFileName, Context.MODE_PRIVATE);

            magicCropped.setHasAlpha(true);
            magicCropped.compress(Constants.IMAGE_COMPRESS_FORMAT, Constants.IMAGE_QUALITY, fos);
            return magicCropped;
        } catch (Throwable e) {
            this.listener.onFailed(e);
            return null;
        }
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
