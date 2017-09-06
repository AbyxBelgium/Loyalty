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

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.ReceivedPermission;
import com.abyx.loyalty.extra.Utils;

/**
 * Abstract class that contains some methods that request permission to use particular features.
 * Every activity that has to use certain permissions could extend this class.
 *
 * TODO this abstraction should be revised!
 *
 * @author Pieter Verschaffelt
 */
public abstract class PermissionActivity extends ToolbarActivity {
    protected final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    protected final int REQUEST_READ_EXTERNAL_STORAGE = 2;
    protected final int REQUEST_CAMERA_ACCESS = 3;

    private ReceivedPermission postWriteExecutor;
    private ReceivedPermission postCameraExecutor;
    private ReceivedPermission postReadExecutor;

    public PermissionActivity(){
        //Empty constructor
    }

    /**
     * @param activity The activity calling this method
     * @param permission The permission for which you want to know if it's granted
     * @return Returns true when the given permission has already been granted by the user
     */
    protected boolean isPermissionGranted(Activity activity, String permission){
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Ask the user for write permissions.
     *
     * @param activity The activity calling this method
     * @param task The object containing the task that should be executed after the write permission
     *             has been granted
     */
    protected void requestWritePermissions(Activity activity, ReceivedPermission task){
        this.postWriteExecutor = task;
        //Check if the permission hasn't been granted by the user already
        if (!isPermissionGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            postWriteExecutor.onPermissionGranted();
        }
    }

    protected void requestCameraPermissions(Activity activity, ReceivedPermission task){
        this.postCameraExecutor = task;
        //Check if the permission hasn't been granted by the user already
        if(!isPermissionGranted(activity, Manifest.permission.CAMERA)){
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_ACCESS);
        } else {
            postCameraExecutor.onPermissionGranted();
        }
    }

    protected void requestReadPermissions(Activity activity, ReceivedPermission task){
        this.postReadExecutor = task;
        if(!isPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            postReadExecutor.onPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    postWriteExecutor.onPermissionGranted();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Utils.showInformationDialog(getString(R.string.write_permission_denied_title),
                            getString(R.string.write_permission_denied_message),
                            PermissionActivity.this,
                            Utils.createDismissListener());
                }
                break;
            }
            case REQUEST_CAMERA_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    postCameraExecutor.onPermissionGranted();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Utils.showInformationDialog(getString(R.string.camera_permission_denied_title),
                            getString(R.string.camera_permission_denied_message),
                            PermissionActivity.this,
                            Utils.createDismissListener());
                }
                break;
            }
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    postReadExecutor.onPermissionGranted();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Utils.showInformationDialog(getString(R.string.read_permission_denied_title),
                            getString(R.string.read_permission_denied_message),
                            PermissionActivity.this,
                            Utils.createDismissListener());
                }
                break;
            }
        }
    }
}
