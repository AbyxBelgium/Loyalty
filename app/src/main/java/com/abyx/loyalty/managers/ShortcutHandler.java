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

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;

import java.util.List;

/**
 * @author Pieter Verschaffelt
 */
public class ShortcutHandler implements ChangeListener<List<Card>> {
    private Context context;

    public static final int POPULAR_SHORTCUT_AMOUNT = 3;

    public ShortcutHandler(Context context) {
        this.context = context;
    }

    /**
     * This method updates the dynamic shortcuts that are handled by this app based upon the most
     * frequently used loyalty cards. This method will only run when invoked on a device with API 25
     * or higher.
     */
    public void updatePopularDynamicShortcuts() {
        if (Build.VERSION.SDK_INT >= 25) {
            Database database = new Database(context);
            database.subscribe(this);
            database.openDatabase();
            database.getAllCardsSortedByHitCount(true);
            database.closeDatabase();
        }
    }

    @Override
    public void change(List<Card> resource) {
        if (Build.VERSION.SDK_INT >= 25) {
            LauncherInfoManager manager = new LauncherInfoManager(context);
            manager.updateDynamicShortcuts(resource, POPULAR_SHORTCUT_AMOUNT);
        }
    }
}
