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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.fragments.CardFragment;
import com.abyx.loyalty.R;
import com.abyx.loyalty.fragments.EditFragment;
import com.abyx.loyalty.managers.DrawableManager;
import com.abyx.loyalty.managers.ShortcutHandler;

/**
 * This Activity represents one card and shows the barcode and a small logo corresponding with this
 * card.
 *
 * @author Pieter Verschaffelt
 */
public class CardActivity extends ToolbarActivity implements EditFragment.EditListener {
    private boolean isEditing;
    private long cardID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent data = getIntent();
        if (data != null) {
            cardID = data.getLongExtra(Constants.INTENT_CARD_ID_ARG, 0);
            CardFragment fragment = CardFragment.newInstance(cardID);
            getSupportFragmentManager().beginTransaction().add(R.id.cardContainer, fragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DrawableManager drawableManager = new DrawableManager();
        Drawable bg = drawableManager.getDrawable(getApplicationContext(), null, android.R.color.transparent);
        //noinspection ConstantConditions
        getSupportActionBar().setBackgroundDrawable(bg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card, menu);
        menu.findItem(R.id.action_edit).setVisible(!isEditing);

        // Pinning shortcuts to launcher is only supported on API 26 or higher.
        if (Build.VERSION.SDK_INT < 26) {
            menu.findItem(R.id.action_pin).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            isEditing = true;
            invalidateOptionsMenu();
            EditFragment fragment = EditFragment.newInstance(cardID);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.cardContainer, fragment).commit();
            fragment.registerListener(this);
        } else if (id == R.id.action_pin) {
            ShortcutHandler handler = new ShortcutHandler(CardActivity.this);
            Database database = new Database(CardActivity.this);
            database.openDatabase();
            handler.setPinnedShortcut(database.getCardByID(cardID));
            database.closeDatabase();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doneEditing() {
        CardFragment fragment = CardFragment.newInstance(cardID, false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.replace(R.id.cardContainer, fragment).commit();
        isEditing = false;
        invalidateOptionsMenu();
    }
}
