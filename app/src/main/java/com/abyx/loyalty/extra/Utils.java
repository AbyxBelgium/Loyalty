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

package com.abyx.loyalty.extra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final int ADD_STORE_SCANNER = 1;
    public static final int ADD_STORE_MANUAL = 2;
    public static final int ADD_STORE = 3;

    public static void showToast(String message, int length, Context context){
        Toast toast = Toast.makeText(context, message, length);
        toast.show();
    }

    public static void showInformationDialog(String title, String message, Context context, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title);
        builder.setPositiveButton("OK", listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean isValidBarcode(String barcode, BarcodeFormat format){
        try {
            Writer barWriter = new MultiFormatWriter();
            barWriter.encode(barcode, format, 1, 1);
            return true;
        } catch (WriterException e) {
            //Empty on purpose
            return false;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    private static class DismissListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    public static DismissListener createDismissListener(){
        return new DismissListener();
    }
}
