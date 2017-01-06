package com.abyx.loyalty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ThumbnailDownloader extends AsyncTask<String, Void, Bitmap> {
    private Context context;
    private String picName;
    private StoreData data;

    public ThumbnailDownloader(Context context, String picName, StoreData data){
        this.picName = picName + "thumb";
        this.context = context;
        this.data = data;
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
