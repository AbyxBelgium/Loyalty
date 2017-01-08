package com.abyx.loyalty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends PermissionActivity {
    private ArrayList<Card> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<>();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (isPermissionGranted(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // Save all data to internal storage
            IO temp = new IO(getApplicationContext());
            temp.save(data);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (isPermissionGranted(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            // Read all data from the internal storage
            IO temp = new IO(MainActivity.this);
            ArrayList<Card> input = temp.load();
            //Performance already optimized from O(n²) to O(n)
            if (input.size() > 0 && !data.contains(input.get(0))) {
                data.addAll(input);
            }
            OverviewFragment fragment = OverviewFragment.newInstance(input);
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }
}
