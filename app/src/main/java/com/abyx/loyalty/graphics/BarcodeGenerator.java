package com.abyx.loyalty.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import be.abyx.aurora.ParallelShapeFactory;
import be.abyx.aurora.RectangleShape;
import be.abyx.aurora.ShapeFactory;

/**
 * This class contains some methods that are used for generating Bitmaps that represent barcode's of
 * some sort.
 *
 * @author Pieter Verschaffelt
 */
public class BarcodeGenerator {
    private Context context;

    public BarcodeGenerator(Context context) {
        this.context = context;
    }

    /**
     * This function will render a barcode (including the digits).
     *
     * @param barcode String representing the data contained by the barcode.
     * @param format The format of the given barcode.
     * @param width The desired width of the resulting Bitmap.
     * @param height The desired height of the resulting Bitmap. Will be increased by this method to
     *               fit with the barcode AND it's included text.
     * @return A Bitmap representing the barcode drawn over a semitransparent background and with
     * the digits included underneath it.
     */
    public Bitmap renderBarcode(String barcode, BarcodeFormat format, int width, int height) throws WriterException {
        // 100 additional pixels are used for rendering text underneath the barcode.
        int textHeight = 100;
        height += textHeight;
        Writer barWriter = new MultiFormatWriter();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitMatrix bm = barWriter.encode(barcode, format, width, height);
        for (int j = 0; j < height - textHeight; j++) {
            int[] row = new int[width];
            for (int i = 0; i < width; i++) {
                row[i] = bm.get(i, j) ? Color.BLACK : Color.TRANSPARENT;
            }
            //We use setPixels to set the pixels of a whole row at once to increase performance
            bitmap.setPixels(row, 0, width, 0, j, width, 1);
        }

        this.renderTextOnBitmap(bitmap, barcode);
        ShapeFactory factory = new ParallelShapeFactory();
        // TODO the background colour should be made a constant
        return factory.createShape(new RectangleShape(this.context), bitmap, Color.argb(143, 175, 175, 175), 10);
    }

    /**
     * Renders an arbitrary string (centered) at the bottom of the given input Bitmap. The given
     * input Bitmap will be altered and must thus be mutable.
     *
     * @param input The Bitmap on which a String should be rendered (This Bitmap must be mutable)
     * @param text The text that should be rendered.
     */
    private void renderTextOnBitmap(Bitmap input, String text) {
        float scale = this.context.getResources().getDisplayMetrics().density;

        int defaultFontSize = 24;

        Canvas canvas = new Canvas(input);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize((int) (defaultFontSize * scale));
        // Change the font to match a more barcode-like font
        paint.setTypeface(Typeface.MONOSPACE);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (input.getWidth() - bounds.width())/2;
        int y = input.getHeight() - 25;

        canvas.drawText(text, x, y, paint);
    }
}
