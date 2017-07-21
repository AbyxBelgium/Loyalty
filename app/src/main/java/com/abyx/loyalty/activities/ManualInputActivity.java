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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.Utils;
import com.google.zxing.BarcodeFormat;

import java.util.List;

public class ManualInputActivity extends AppCompatActivity {
    private EditText barcodeText;
    private Spinner formatSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
        barcodeText = (EditText) findViewById(R.id.barcodeText);
        formatSpinner = (Spinner) findViewById(R.id.formatSpinner);
        System.out.println("Formatspinner: " + formatSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.format_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        formatSpinner.setAdapter(adapter);
        formatSpinner.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if (barcodeText.getText().toString().equals("")){
                barcodeText.setError(getString(R.string.wrong_barcode_input));
            } else {
                if (Utils.isValidBarcode(barcodeText.getText().toString(), BarcodeFormat.valueOf(formatSpinner.getSelectedItem().toString()))){
                    Intent intent = new Intent(ManualInputActivity.this, FinishActivity.class);
                    Intent created = getIntent();
                    Card card = new Card(created.getStringExtra("STORENAME"), barcodeText.getText().toString(), BarcodeFormat.valueOf(formatSpinner.getSelectedItem().toString()), 0);
                    intent.putExtra(Constants.INTENT_CARD_ARG, card);
                    startActivityForResult(intent, Utils.ADD_STORE);
                } else {
                    barcodeText.setError(getString(R.string.wrong_barcode_input));
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
