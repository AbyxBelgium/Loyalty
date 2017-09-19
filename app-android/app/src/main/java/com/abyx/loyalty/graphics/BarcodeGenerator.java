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

package com.abyx.loyalty.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.Constants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import be.abyx.aurora.FactoryManager;
import be.abyx.aurora.shapes.ParallelShapeFactory;
import be.abyx.aurora.shapes.RectangleShape;
import be.abyx.aurora.shapes.ShapeFactory;
import be.abyx.aurora.utilities.CropUtility;

/**
 * This class contains some methods that are used for generating Bitmaps that represent barcode's of
 * some sort.
 *
 * @author Pieter Verschaffelt
 */
public class BarcodeGenerator {
    private Context context;
    private int defaultFontSize = 24;
    private int padding = 10;

    public BarcodeGenerator(Context context) {
        this.context = context;
        defaultFontSize = context.getResources().getInteger(R.integer.barcode_font_size);
    }

    /**
     * This function will render a barcode (including the digits). When the type of the BarcodeFormat
     * is QR_CODE, no digits will be rendered underneath the code and only width will be used as
     * a dimension (thus returning a square Bitmap).
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
        if (format == BarcodeFormat.QR_CODE) {
            return renderQRCode(barcode, format, width);
        } else {
            return renderOrdinaryBarcode(barcode, format, width, height);
        }
    }

    private Bitmap renderOrdinaryBarcode(String barcode, BarcodeFormat format, int width, int height) throws WriterException {
        float scale = this.context.getResources().getDisplayMetrics().density;

        // 100 additional pixels are used for rendering text underneath the barcode.
        int textHeight = 100;
        height += textHeight;

        height *= scale;
        width *= scale;

        Writer barWriter = new MultiFormatWriter();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitMatrix bm = barWriter.encode(barcode, format, width, height);

        int textPosX = getTextPositionX(barcode, width);
        int textWidth = getTextWidth(barcode);

        for (int j = 0; j < height - textHeight; j++) {
            int[] row = new int[width];
            for (int i = 0; i < width; i++) {
                row[i] = bm.get(i, j) ? Color.BLACK : Color.TRANSPARENT;
            }
            //We use setPixels to set the pixels of a whole row at once to increase performance
            bitmap.setPixels(row, 0, width, 0, j, width, 1);
        }

        for (int j = height - textHeight; j < height - 10; j++) {
            int[] row = new int[width];
            for (int i = 0; i < width; i++) {
                if (i < (textPosX - padding * 2) || i > (textPosX + textWidth + padding * 2)) {
                    row[i] = bm.get(i, j) ? Color.BLACK : Color.TRANSPARENT;
                }
            }
            bitmap.setPixels(row, 0, width, 0, j, width, 1);
        }

        this.renderTextOnBitmap(bitmap, barcode);
        FactoryManager manager = new FactoryManager();
        ShapeFactory factory = manager.getRecommendedShapeFactory();
        return factory.createShape(new RectangleShape(this.context), bitmap, Constants.LOGO_BACKGROUND_COLOUR, padding);
    }

    private Bitmap renderQRCode(String barcode, BarcodeFormat format, int width) throws WriterException {
        float scale = this.context.getResources().getDisplayMetrics().density;

        width *= scale * 0.6;

        Writer barWriter = new MultiFormatWriter();
        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        BitMatrix bm = barWriter.encode(barcode, format, width, width);

        for (int j = 0; j < width; j++) {
            int[] row = new int[width];
            for (int i = 0; i < width; i++) {
                row[i] = bm.get(i, j) ? Color.BLACK : Color.TRANSPARENT;
            }
            bitmap.setPixels(row, 0, width, 0, j, width, 1);
        }

        CropUtility cropUtility = new CropUtility();
        Bitmap cropped = cropUtility.rectangularCrop(bitmap, Color.TRANSPARENT, 0);

        FactoryManager manager = new FactoryManager();
        ShapeFactory shapeFactory = manager.getRecommendedShapeFactory();
        return shapeFactory.createShape(new RectangleShape(this.context), cropped, Constants.LOGO_BACKGROUND_COLOUR, padding);
    }

    /**
     * Renders an arbitrary string (centered) at the bottom of the given input Bitmap. The given
     * input Bitmap will be altered and must thus be mutable.
     *
     * @param input The Bitmap on which a String should be rendered (This Bitmap must be mutable)
     * @param text The text that should be rendered.
     */
    private void renderTextOnBitmap(Bitmap input, String text) {

        Canvas canvas = new Canvas(input);

        Paint paint = getBarcodePaint();

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (input.getWidth() - bounds.width())/2;
        int y = input.getHeight() - 25;

        canvas.drawText(text, x, y, paint);
    }

    private Paint getBarcodePaint() {
        float scale = this.context.getResources().getDisplayMetrics().density;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize((int) (defaultFontSize * scale));
        // Change the font to match a more barcode-like font
        Typeface font = Typeface.createFromAsset(context.getAssets(), "font/terminal.ttf");
        paint.setTypeface(font);
        return paint;
    }

    private int getTextPositionX(String text, int bitmapWidth) {
        Rect bounds = new Rect();
        getBarcodePaint().getTextBounds(text, 0, text.length(), bounds);
        return (bitmapWidth - bounds.width()) / 2;
    }

    private int getTextWidth(String text) {
        Rect bounds = new Rect();
        getBarcodePaint().getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    private int getTextPositionY(String text, int bitmapHeight) {
        Rect bounds = new Rect();
        getBarcodePaint().getTextBounds(text, 0, text.length(), bounds);
        return bitmapHeight - 25;
    }
}
