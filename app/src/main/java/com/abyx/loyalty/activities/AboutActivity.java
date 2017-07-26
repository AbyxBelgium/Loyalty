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

package com.abyx.loyalty.activities;

import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.abyx.loyalty.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This activity shows the about screen where the user can find all sorts of developer related
 * information. This includes the app's version number, legal information and so on...
 *
 * @author Pieter Verschaffelt
 */
public class AboutActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //Version number is loaded programmatically because this isn't supported as an operation
        //in the layout files.
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView) findViewById(R.id.versionField)).setText("Loyalty v" + versionName);
        } catch (PackageManager.NameNotFoundException e){
            //The version number is simply not shown when something goes wrong. No further
            //information has to be given here.
        }
        TextView licenseTextView = (TextView) findViewById(R.id.licenseTextView);
        licenseTextView.setText(getLicenseText());
    }

    private String getLicenseText() {
        InputStream inputStream = getResources().openRawResource(R.raw.license);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                builder.append("\n");
                line = reader.readLine();
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
