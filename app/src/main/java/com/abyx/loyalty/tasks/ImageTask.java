package com.abyx.loyalty.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.abyx.loyalty.extra.ProgressIndicator;
import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.Utils;
import com.abyx.loyalty.contents.Card;

/**
 * This abstract class contains some exception handling code and common constructors
 * that are very handy for some Image-handling classes.
 *
 * @author Pieter Verschaffelt
 */
public abstract class ImageTask extends AsyncTask<String, Void, Bitmap> {
    protected ImageView bmImage;
    protected Context context;
    protected String picName;
    protected Card data;
    protected ProgressIndicator progressInd;
    protected boolean showError;

    public ImageTask(ImageView bmImage, Context context, String picName, Card data){
        initialize(bmImage, context, picName, data);
        this.showError = false;
    }

    public ImageTask(ImageView bmImage, Context context, String picName, Card data, boolean showError){
        initialize(bmImage, context, picName, data);
        this.showError = showError;
    }

    /**
     * Set a ProgressIndicator that should be controlled by this ImageTask. The ProgressIndicator
     * will be informed when the task finishes downloading and converting the image.
     *
     * @param progressInd The ProgressIndicator that should be informed when the task completes
     */
    public void setProgressIndicator(ProgressIndicator progressInd){
        this.progressInd = progressInd;
    }

    /**
     * This function initializes all local variables with the data passed as arguments to this
     * function.
     *
     * @param bmImage The imageview that has to be used to show the bitmap image
     * @param context The application context that originally called this function
     * @param picName The name of the image that has to be loaded onto the imageview
     * @param data The Card object that belongs to this image.
     */
    protected void initialize(ImageView bmImage, Context context, String picName, Card data){
        this.bmImage = bmImage;
        this.context = context;
        this.picName = picName;
        this.data = data;
    }

    /**
     * This function is called after execution on the second thread is done. When connection to
     * the Loyalty API failed or another I/O exception occurred, an error message is shown.
     * This message warns the user that something went wrong and thus asks him to manually provide
     * a logo url or go on with the default logo. This function simply sets the created bitmap
     * to the ImageView when nothing went wrong.
     *
     * @param result The bitmap that has been created by the parallel thread
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            bmImage.setImageBitmap(result);
        } else {
            bmImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.basket));
            data.setDefaultImageLocation();
            if (showError) {
                String title = context.getString(R.string.not_found_error_title);
                String message = context.getString(R.string.not_found_error_message);
                Utils.showInformationDialog(title, message, context, Utils.createDismissListener());
            }
        }

        if (progressInd != null){
            progressInd.setDone(true);
        }
    }
}
