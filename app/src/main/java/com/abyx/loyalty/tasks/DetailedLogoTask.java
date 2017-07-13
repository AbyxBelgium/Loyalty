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
import com.abyx.loyalty.extra.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import be.abyx.aurora.CircleShape;
import be.abyx.aurora.ImageUtils;
import be.abyx.aurora.ParallelShapeFactory;
import be.abyx.aurora.ShapeFactory;

/**
 * Retrieves the logo corresponding to this store that has been processed with a magic crop and that
 * is placed above a circular, semi-transparent, background.
 *
 * @author Pieter Verschaffelt
 */
public class DetailedLogoTask extends AsyncTask<Bitmap, Void, Bitmap> {
    private Context context;
    private TaskListener<Bitmap> listener;
    private Card card;

    public DetailedLogoTask(Context context, TaskListener<Bitmap> listener, Card card) {
        this.context = context;
        this.listener = listener;
        this.card = card;
    }

    /**
     * This method will produce the desired logo on circular background.
     *
     * @param params An array of length one that contains the raw logo Bitmap. This Bitmap will then
     *               be used as the foreground layer for the output Bitmap.
     * @return A circular logo placed upon a semi-transparent background.
     */
    @Override
    @Nullable
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap output;

        Bitmap logo = params[0];
        ImageUtils utils = new ImageUtils(this.context);
        Bitmap croppedLogo = utils.magicCrop(logo, Color.WHITE, Constants.MAGIC_CROP_TOLERANCE);
        ShapeFactory shapeFactory = new ParallelShapeFactory();
        output = shapeFactory.createShape(new CircleShape(this.context), croppedLogo, Constants.LOGO_BACKGROUND_COLOUR, 150);

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
