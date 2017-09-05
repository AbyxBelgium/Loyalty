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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;

import com.abyx.loyalty.R;
import com.abyx.loyalty.activities.CardActivity;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * This manager is responsible for handling all shortcut related actions for this app. This manager
 * can only be used on API 25 or higher.
 *
 * @author Pieter Verschaffelt
 */
@TargetApi(25)
public class LauncherInfoManager {
    private Context context;
    private List<Card> cards;

    public LauncherInfoManager(Context context) {
        this.context = context;
    }

    /**
     * Update the dynamic shortcuts that are associated with this app. The given list of cards must
     * be sorted by popularity. The amount parameter indicates how much shortcuts should be made.
     *
     * @param cards A list of cards that's sorted by popularity.
     * @param amount Amount indicates the number n of cards for which a shortcut should be made from
     *               the list.
     */
    public void updateDynamicShortcuts(List<Card> cards, int amount) {
        if (cards.size() < amount) {
            amount = cards.size();
        }

        this.cards = cards;

        List<ShortcutInfo> shortcuts = new ArrayList<>();

        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);


        for (int i = 0; i < amount; i++) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.EMPTY, context, CardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constants.INTENT_CARD_ID_ARG, cards.get(i).getID());

            ShortcutInfo shortcut = new ShortcutInfo.Builder(context, cards.get(i).getName() + "-shortcut")
                    .setShortLabel(cards.get(i).getName())
                    .setIcon(Icon.createWithResource(context, R.drawable.shortcut_store))
                    .setIntent(intent)
                    .build();
            shortcuts.add(shortcut);
        }

        shortcutManager.setDynamicShortcuts(shortcuts);
    }
}
