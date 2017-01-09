package com.abyx.loyalty.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Abstract class containing code that generates bitmaps from barcodes and their value.
 *
 * TODO: THIS ABSTRACTION SHOULD BE REVISED SINCE ACTIVITIES ARE NOW FRAGMENTED INTO FRAGMENTS
 *
 * @author Pieter Verschaffelt
 */
public abstract class DetailedActivity extends PermissionActivity {
    protected Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This function returns a bitmap that's generated using the given barcode (as a string) and
     * it's format.
     *
     * @param barcode The value of the barcode that has to be processed into a bitmap
     * @param format The format of the barcode that has to be processed into a bitmap
     * @return A bitmap representing the given barcode and it's format
     */
    public Bitmap encodeAsBitmap(String barcode, BarcodeFormat format){
        long start = System.currentTimeMillis();
        Writer barWriter = new MultiFormatWriter();
        System.out.println("Create MultiFormatWriter: " + (System.currentTimeMillis() - start) + "ms");
        int width = 700;
        int height = 300;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        System.out.println("Create bitmap: " + (System.currentTimeMillis() - start) + "ms");
        try {
            BitMatrix bm = barWriter.encode(barcode, format, width, height);
            System.out.println("Encode: " + (System.currentTimeMillis() - start) + "ms");
            for (int j = 0; j < height; j++) {
                int[] row = new int[width];
                for (int i = 0; i < width; i++) {
                    row[i] = bm.get(i, j) ? Color.BLACK : Color.WHITE;
                }
                //We use setPixels to set the pixels of a whole row at once to increase performance
                bitmap.setPixels(row, 0, width, 0, j, width, 1);
            }
            System.out.println("SetPixels: " + (System.currentTimeMillis() - start) + "ms");
        } catch (WriterException e){
            System.err.println("An error occured: " + e);
        }
        return bitmap;
    }
}
