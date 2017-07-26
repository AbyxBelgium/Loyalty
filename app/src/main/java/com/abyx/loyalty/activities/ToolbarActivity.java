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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.abyx.loyalty.R;

/**
 * This Activity-class contains some common code that's shared by all Activities that display a
 * toolbar.
 *
 * @author Pieter Verschaffelt
 */
public class ToolbarActivity extends AppCompatActivity {
    protected boolean setDisplayHomeAsUp = true;

    @Override
    protected void onResume() {
        super.onResume();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(setDisplayHomeAsUp);
        getSupportActionBar().setDisplayShowHomeEnabled(setDisplayHomeAsUp);
    }
}
