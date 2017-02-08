package com.abyx.loyalty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.fragments.CardFragment;
import com.abyx.loyalty.R;
import com.abyx.loyalty.fragments.EditFragment;

/**
 * This Activity represents one card and shows the barcode and a small logo corresponding with this
 * card.
 *
 * @author Pieter Verschaffelt
 */
public class CardActivity extends AppCompatActivity implements EditFragment.EditListener {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card, menu);
        menu.findItem(R.id.action_edit).setVisible(!isEditing);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doneEditing() {
        CardFragment fragment = CardFragment.newInstance(cardID);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.replace(R.id.cardContainer, fragment).commit();
        isEditing = false;
        invalidateOptionsMenu();
    }
}
