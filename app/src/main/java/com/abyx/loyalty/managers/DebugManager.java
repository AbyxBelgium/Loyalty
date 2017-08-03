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
import android.widget.Toast;

import com.abyx.loyalty.BuildConfig;

/**
 * This Manager can be used for performing debug related actions. The methods that are available
 * in this DebugManager will only be invoked when the app is being compiled in the debug mode.
 *
 * @author Pieter Verschaffelt
 */
public class DebugManager {
    // All messages that are sent to the console via this DebugManager will also be presented to the
    // user as a toast when set to true. (True by default).
    private static boolean showAsToast = true;
    private static int toastLength = Toast.LENGTH_SHORT;

    private DebugManager() {}

    public static void debugPrint(String message, Context context) {
        if (inDebugMode()) {
            System.out.println("NOTE: " + message);
            if (showAsToast) {
                Toast toast = Toast.makeText(context, message, toastLength);
                toast.show();
            }
        }
    }

    public static void setShowToast(boolean show) {
        showAsToast = show;
    }

    public static void setToastLength(int length) {
        toastLength = length;
    }

    private static boolean inDebugMode() {
        return BuildConfig.DEBUG;
    }
}
