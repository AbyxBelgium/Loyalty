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

package com.abyx.loyalty.managers;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.IOException;

/**
 * This manager is responsible for managing all accesses to external directories. It encapsulates
 * differences that are the result of changes in the way external files have to be handled in
 * different API versions.
 *
 * @author Pieter Verschaffelt
 */
public class FileManager {
    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    public File getExternalFilesDir() throws IOException {
        File tempFile = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // compatible for ALL the versions
                File[] dirs = ContextCompat.getExternalFilesDirs(context, null); //null, no specific sub directory
                if (dirs.length > 0) {
                    tempFile  = dirs[dirs.length -1];
                }
            }
        } else {
            tempFile = Environment.getExternalStorageDirectory();
        }

        if (tempFile == null) {
            throw new IOException("External file could not be initialized!");
        } else {
            return tempFile;
        }
    }
}
