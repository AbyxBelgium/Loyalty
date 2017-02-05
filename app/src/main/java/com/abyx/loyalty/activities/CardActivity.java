package com.abyx.loyalty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.fragments.CardFragment;
import com.abyx.loyalty.R;

/**
 * This Activity represents one card and shows the barcode and a small logo corresponding with this
 * card.
 *
 * @author Pieter Verschaffelt
 */
public class CardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent data = getIntent();
        if (data != null) {
            CardFragment fragment = CardFragment.newInstance((Card) data.getParcelableExtra(Constants.INTENT_CARD_ARG));
            getSupportFragmentManager().beginTransaction().add(R.id.cardContainer, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent = new Intent(CardActivity.this, EditActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }
}
