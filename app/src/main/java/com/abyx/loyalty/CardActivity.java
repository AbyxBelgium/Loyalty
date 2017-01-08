package com.abyx.loyalty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.google.zxing.BarcodeFormat;

/**
 * This activity manages the interaction and view of a CardFragment.
 *
 * @author Pieter Verschaffelt
 */

public class CardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        Card temp = new Card("Delhaize", "460970374790", "https://nl.delhaize.be/-/media/files/press/media%20library/logos/logo_delhaize_67.jpg", BarcodeFormat.UPC_A);
        CardFragment fragment = CardFragment.newInstance(temp);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment).commit();
    }
}
