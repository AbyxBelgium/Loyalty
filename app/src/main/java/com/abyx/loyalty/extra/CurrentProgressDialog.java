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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abyx.loyalty.R;

/**
 * Dialog that shows the current progress that already has been elapsed.
 * This CurrentProgressDialog is an improvement over the ProgressDialog because it complies with
 * Google's newest design guidelines.
 *
 * @author Pieter Verschaffelt
 */
public class CurrentProgressDialog extends AlertDialog {
    private ProgressBar progressBar;
    private TextView progressField;
    private TextView titleField;

    public CurrentProgressDialog(Context context){
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_current_progress);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressField = (TextView) findViewById(R.id.progressField);
        this.titleField = (TextView) findViewById(R.id.titleField);
    }

    public void setTitle(String title){
        titleField.setText(title);
    }

    public void setProgress(int progress){
        progressBar.setProgress(progress);
        progressField.setText(String.format("%.2f", ((double) progress / getMax()) * 100) + "%");
    }

    public void setMax(int max){
        progressBar.setMax(max);
    }

    public int getMax(){
        return progressBar.getMax();
    }
}
