package com.abyx.loyalty.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.widget.Toast;

import com.abyx.loyalty.extra.CurrentProgressDialog;
import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Task that downloads all logos to the device's internal storage from the given list.
 *
 * @author Pieter Verschaffelt
 */
public class RestoreDataTask extends AsyncTask<String, Integer, Void> {
    private Context context;
    private List<Card> data;
    private CurrentProgressDialog progressDialog;

    public RestoreDataTask(Context context, List<Card> data, CurrentProgressDialog progressDialog){
        this.context = context;
        this.data = data;
        this.progressDialog = progressDialog;
        this.progressDialog.setMax(data.size()*2);
    }

    protected Void doInBackground(String... params){
        int i = 0;
        for(Card current: data) {
            String urldisplay = current.getImageURL();
            System.out.println(current.getName());
            Bitmap mIcon11;
            try {
                System.out.println("Starting...");
                File file = context.getFileStreamPath(current.getImageLocation());
                if (file == null || !file.exists()) {
                    System.out.println("Not null!");
                    //Download full res image
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    FileOutputStream fos = context.openFileOutput(current.getImageLocation(), Context.MODE_PRIVATE);
                    mIcon11.compress(Bitmap.CompressFormat.PNG, 75, fos);
                    i++;
                    publishProgress(i);
                    //Download and generate thumbnail
                    in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    fos = context.openFileOutput(current.getImageLocation() + "thumb", Context.MODE_PRIVATE);
                    mIcon11 = scaleBitmap(mIcon11, 250, 250);
                    mIcon11.compress(Bitmap.CompressFormat.PNG, 75, fos);
                    i++;
                    publishProgress(i);
                }
            } catch (IOException | IllegalArgumentException | NullPointerException e) {
                System.err.println(e);
            }
        }
        return null;
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

    protected void onProgressUpdate(Integer... progress) {
        this.progressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result){
        this.progressDialog.dismiss();
        Utils.showToast(context.getString(R.string.successful_restore), Toast.LENGTH_SHORT, context);
    }
}
