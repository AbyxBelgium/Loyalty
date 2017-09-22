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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.abyx.loyalty.R;
import com.abyx.loyalty.activities.CardActivity;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.tasks.LogoTask;
import com.abyx.loyalty.tasks.TaskListener;

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
            ShortcutInfo shortcut = new ShortcutInfo.Builder(context, cards.get(i).getName() + "-shortcut")
                    .setShortLabel(cards.get(i).getName())
                    .setIcon(Icon.createWithResource(context, R.drawable.shortcut_store))
                    .setIntent(createCardShortcutIntent(cards.get(i)))
                    .build();
            shortcuts.add(shortcut);
        }

        shortcutManager.setDynamicShortcuts(shortcuts);
    }

    /**
     * Make a pinned shortcut for the given card. The logo associated with this card will be used
     * as Icon. This function is only available on API 26 or higher.
     *
     * @param card The card for whom a pinned shortcut should be made.
     */
    @TargetApi(26)
    public void setPinnedShortcut(Card card) {
        LogoTask logoTask = new LogoTask(context, new LogoTaskListener(card));
        logoTask.execute(card);
    }

    @TargetApi(26)
    private void createPinnedShortcut(Card card, Icon icon) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

        if (shortcutManager.isRequestPinShortcutSupported()) {
            ShortcutInfo pin = new ShortcutInfo.Builder(context, card.getName() + "_pin")
                    .setShortLabel(card.getName())
                    .setIntent(createCardShortcutIntent(card))
                    .setIcon(icon)
                    .build();
            shortcutManager.requestPinShortcut(pin, null);
        }
    }

    @TargetApi(26)
    public void removePinnedShortcut(Card card) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

        if (shortcutManager.isRequestPinShortcutSupported()) {
            List<String> toRemove = new ArrayList<>();
            toRemove.add(card.getName() + "_pin");
            shortcutManager.disableShortcuts(toRemove, context.getString(R.string.error_no_longer_in_library));
        }
    }

    /**
     * Create an Intent that instructs Android to open up the CardActivity with all data from the
     * desired card.
     *
     * @param card Card that should be shown in CardActivity.
     * @return An Intent that's capable of opening up the CardActivity and showing all of this
     * Card's information.
     */
    private Intent createCardShortcutIntent(Card card) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.EMPTY, context, CardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.INTENT_CARD_ID_ARG, card.getID());

        return intent;
    }

    private class LogoTaskListener implements TaskListener<Bitmap> {
        public Card card;

        public LogoTaskListener(Card card) {
            this.card = card;
        }

        @Override
        public void onProgressUpdate(double progress) {
            // Nothing to do here!
        }

        @Override
        public void onFailed(@Nullable Throwable exception) {
            // Use default store logo
            createPinnedShortcut(card, Icon.createWithResource(context, R.drawable.shortcut_store));
        }

        @Override
        public void onDone(Bitmap result) {
            createPinnedShortcut(card, Icon.createWithBitmap(result));
        }
    }
}
