package com.abyx.loyalty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.contents.IO;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.fragments.CardFragment;

import java.util.List;

/**
 * The FinishActivity is responsible for controlling the last screen visible to the user when adding
 * a new Loyalty card.
 *
 * @author Pieter Verschaffelt
 */

public class FinishActivity extends AppCompatActivity {
    private Card card;
    private Database db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Intent data = getIntent();
        if (data != null) {
            card = data.getParcelableExtra(Constants.INTENT_CARD_ARG);
            db = new Database(getApplicationContext());
            db.openDatabase();
            db.addCard(card);
            CardFragment fragment = CardFragment.newInstance(card.getID());
            getSupportFragmentManager().beginTransaction().add(R.id.cardContainer, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finish, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Remove card and go back to MainActivity
        db.deleteCard(card);
        Intent intent = new Intent(FinishActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        db.closeDatabase();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done){
            Intent intent = new Intent(FinishActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            // Parent button was clicked (Delete the card from the database!)
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
