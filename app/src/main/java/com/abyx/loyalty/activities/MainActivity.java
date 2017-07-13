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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.ReceivedPermission;
import com.abyx.loyalty.fragments.CardFragment;
import com.abyx.loyalty.fragments.Filter;
import com.abyx.loyalty.fragments.ListFragment;
import com.abyx.loyalty.fragments.ListInteractor;
import com.abyx.loyalty.fragments.OverviewFragment;
import com.abyx.loyalty.R;
import com.abyx.loyalty.managers.ChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends PermissionActivity implements ListInteractor<Card>, ChangeListener<List<Card>> {
    private static final String sortedString = "sorted_descending";

    private ArrayList<Card> data;
    private boolean sortedDescending;
    private ListFragment<Card> overviewFragment;

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The sortedDescending value from the last time the user used this app
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sortedDescending = sharedPref.getBoolean(sortedString, true);

        overviewFragment = OverviewFragment.newInstance();

        this.db = new Database(getApplicationContext());
        this.db.subscribe(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        this.db.unsubscribe(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (isPermissionGranted(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            loadData();
        } else {
            requestWritePermissions(MainActivity.this, new ReceivedPermission() {
                @Override
                public void onPermissionGranted() {
                    loadData();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort){
            db.openDatabase();
            db.getAllCardsSorted(sortedDescending);
            db.closeDatabase();

            sortedDescending = !sortedDescending;
            // Save the sort preference of the user, so he doesn't has to choose this every time
            // the app starts
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(sortedString, sortedDescending);
            editor.apply();
            return true;
        } else if (id == R.id.action_about) {
            Intent temp = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(temp);
            return true;
        } else if (id == R.id.action_backup) {
            Intent temp = new Intent(MainActivity.this, BackupRestoreActivity.class);
            temp.putParcelableArrayListExtra(Constants.INTENT_LIST_ARG, data);
            startActivity(temp);
            return true;
        } else if (id == R.id.search) {
            Intent temp = new Intent(MainActivity.this, SearchResultsActivity.class);
            temp.putParcelableArrayListExtra(Constants.INTENT_LIST_ARG, data);
            startActivity(temp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<Card> requestData() {
        if (this.data == null) {
            return new ArrayList<>();
        } else {
            return data;
        }
    }

    @Override
    public void onItemClick(int dataPos, Card item) {
        Intent intent = new Intent(MainActivity.this, CardActivity.class);
        intent.putExtra(Constants.INTENT_CARD_ID_ARG, item.getID());
        startActivity(intent);
    }

    @Override
    public void change(List<Card> resource) {
        this.data = (ArrayList<Card>) resource;
        overviewFragment.dataChanged(resource);
    }

    private void loadData() {
        // Read all data from the internal storage
        db.openDatabase();
        db.getAllCardsSorted(sortedDescending);
        db.closeDatabase();

        getSupportFragmentManager().beginTransaction().replace(R.id.overviewContainer, overviewFragment).commit();
    }

    /**
     * This function is called when the add-button (the FAB) is pressed.
     *
     * @param view The view that was clicked and triggered this method.
     */
    public void addData(View view) {
        Intent intent = new Intent(MainActivity.this, AddStoreActivity.class);
        startActivity(intent);
    }
}
