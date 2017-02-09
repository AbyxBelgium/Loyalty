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
package com.abyx.loyalty.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.ImageView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.tasks.ImageTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pieter on 26/07/2015.
 */
public class ThumbnailImageTask extends ImageTask {

    public ThumbnailImageTask(ImageView bmImage, Context context, String picName, Card data) {
        super(bmImage, context, picName + "thumb", data);
    }

    public ThumbnailImageTask(ImageView bmImage, Context context, String picName, Card data, boolean showError){
        super(bmImage, context, picName + "thumb", data, showError);
        this.picName = this.picName + "thumb";
    }

    protected Bitmap doInBackground(String... urls){
        String urldisplay = urls[0];
        Bitmap mIcon11;
        try {
            File file = context.getFileStreamPath(picName);
            if (file == null || !file.exists()) {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                FileOutputStream fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
                mIcon11 = scaleBitmap(mIcon11, 250, 250);
                mIcon11.compress(Bitmap.CompressFormat.PNG, 75, fos);
            } else {
                FileInputStream fis = context.openFileInput(picName);
                mIcon11 = BitmapFactory.decodeStream(fis);
                fis.close();
            }
        } catch (IOException | IllegalArgumentException | NullPointerException e){
            return null;
        }
        return mIcon11;
    }

    private Bitmap scaleBitmap(Bitmap originalImage, int width, int height){
        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        float originalWidth = originalImage.getWidth(), originalHeight = originalImage.getHeight();
        Canvas canvas = new Canvas(background);
        float scale = width/originalWidth;
        float xTranslation = 0.0f, yTranslation = (height - originalHeight * scale)/2.0f;
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(originalImage, transformation, paint);
        return background;
    }
}
