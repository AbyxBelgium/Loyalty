package com.abyx.loyalty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abyx.loyalty.contents.Card;
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
            CardFragment fragment = CardFragment.newInstance((Card) data.getParcelableExtra(MainActivity.CARD_INTENT_ARG));
            getSupportFragmentManager().beginTransaction().add(R.id.cardContainer, fragment).commit();
        }
    }
}
