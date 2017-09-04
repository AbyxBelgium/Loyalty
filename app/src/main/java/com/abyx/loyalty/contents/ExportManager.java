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

package com.abyx.loyalty.contents;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.abyx.loyalty.exceptions.InvalidImportFile;
import com.abyx.loyalty.exceptions.MakeDirException;
import com.abyx.loyalty.managers.FileManager;
import com.google.zxing.BarcodeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the export and import of cards to / from an external file.
 *
 * @author Pieter Verschaffelt
 */
public class ExportManager {
    private Context context;

    public ExportManager(Context context) {
        this.context = context;
    }

    /**
     * Parse the data contained in the given stream and return a list of cards that was read from
     * that stream.
     *
     * @param stream A stream that gives access to the contents of the file to be read.
     * @return A list of cards that were found in the given file.
     * @throws IOException Whenever something goes wrong while reading the file.
     * @throws InvalidImportFile When the given fileUrl does not point to a valid Loyalty file.
     */
    public List<Card> getContents(InputStream stream) throws IOException, InvalidImportFile {
        List<Card> output = new ArrayList<>();

        try {
            BufferedReader buffered = new BufferedReader(new InputStreamReader(stream));
            String line = buffered.readLine();
            while (line != null) {
                String[] rawData = line.split("\t");

                if (rawData.length != 4) {
                    throw new InvalidImportFile();
                }

                // Keep compatibility with the previous version of Loyalty and thus keep 4 items
                // for every card (that's why index 3 is still used).
                Card temp = new Card(rawData[0], rawData[1], BarcodeFormat.valueOf(rawData[3]), 0);
                output.add(temp);
                line = buffered.readLine();
            }
            buffered.close();
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong while reading the file", e);
        }

        return output;
    }

    public void exportContents(List<Card> data) throws IOException {
        FileManager manager = new FileManager(context);

        File file = new File(manager.getExternalFilesDir(), "Loyalty");

        // Make directory if it doesn't exist yet
        if (!file.exists() && !file.mkdirs()){
            throw new MakeDirException("Could not create directory to save backup files!");
        }

        file = new File(file, "backup.ly");
        file.createNewFile();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(getDataToWrite(data));
        writer.close();
    }

    private String getDataToWrite(List<Card> data){
        String start = "";
        for (Card content: data){
            start += content.getName() + "\t" + content.getBarcode() + "\t" + content.getImageURL() + "\t" + content.getFormat().toString() ;
            start += "\n";
        }
        return start;
    }
}
