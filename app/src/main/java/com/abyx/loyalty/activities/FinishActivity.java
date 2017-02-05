package com.abyx.loyalty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.fragments.CardFragment;

/**
 * The FinishActivity is responsible for controlling the last screen visible to the user when adding
 * a new Loyalty card.
 *
 * @author Pieter Verschaffelt
 */

public class FinishActivity extends AppCompatActivity {
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
}
