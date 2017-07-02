/**
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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.ReceivedPermission;

/**
 * This activity is responsible for adding new stores to the userdata. This activity itself
 * asks the user for the store name and then starts a new activity containing a barcode scanner.
 * The barcode scanner delivers its results back to this activity where everything is processed and
 * checked.
 *
 * @author Pieter Verschaffelt
 */
public class AddStoreActivity extends PermissionActivity {
    private EditText storeName;
    private FloatingActionButton scanButton;
    private FloatingActionButton enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        storeName = (EditText) findViewById(R.id.storeName);
        scanButton = (FloatingActionButton) findViewById(R.id.scanButton);
        enterButton = (FloatingActionButton) findViewById(R.id.enterButton);
        PackageManager pm = getApplicationContext().getPackageManager();
        //Remove the "scan barcode" button if the device doesn't have a camera
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            ViewGroup layout = (ViewGroup) scanButton.getParent();
            layout.removeView(scanButton);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * Function called when the user clicks the done button. This function checks whether the
     * input is legal and refuses to go through when this isn't the case.
     */
    public void scanBarcode(View view){
        if (storeName.getText().toString().equals("")) {
            storeName.setError(getString(R.string.empty_store_name));
        } else {
            requestCameraPermissions(AddStoreActivity.this, new ReceivedPermission() {
                @Override
                public void onPermissionGranted() {
                Intent intent = new Intent(AddStoreActivity.this, ScannerActivity.class);
                intent.putExtra("STORENAME", storeName.getText().toString());
                startActivity(intent);
                }
            });
        }
    }

    public void enterBarcode(View view){
        if (storeName.getText().toString().equals("")) {
            storeName.setError(getString(R.string.empty_store_name));
        } else {
            Intent intent = new Intent(AddStoreActivity.this, ManualInputActivity.class);
            intent.putExtra("STORENAME", storeName.getText().toString());
            startActivity(intent);
        }
    }
}
