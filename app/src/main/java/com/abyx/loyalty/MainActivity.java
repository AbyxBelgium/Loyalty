package com.abyx.loyalty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends PermissionActivity implements OverviewFragment.OverviewFragmentInteractionListener{
    public static final String CARD_INTENT_ARG = "CARD";
    public static final String BACKUP_INTENT_ARG = "LIST";
    private static final String sortedString = "sorted_descending";

    private FrameLayout cardContainer;
    private MenuItem sortButton;

    private ArrayList<Card> data;
    private boolean sortedDescending;
    private OverviewFragment overviewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardContainer = (FrameLayout) findViewById(R.id.cardContainer);

        data = new ArrayList<>();

        //The sortedDescending value from the last time the user used this app
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sortedDescending = sharedPref.getBoolean(sortedString, true);
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

            if (input.size() > 0 && !data.contains(input.get(0))) {
                data.addAll(input);
            }

            // Sort data according to the previous order
            sort(sortedDescending);
            overviewFragment = OverviewFragment.newInstance(input);
            getSupportFragmentManager().beginTransaction().add(R.id.overviewContainer, overviewFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        sortButton = menu.findItem(R.id.action_sort);
        setSortIcon(sortedDescending);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort){
            reverse();
            sortedDescending = !sortedDescending;
            setSortIcon(sortedDescending);
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
            temp.putParcelableArrayListExtra(BACKUP_INTENT_ARG, data);
            startActivity(temp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(Card card) {
        // Device is big enough and orientated in landscape when cardContainer exists!
        if (cardContainer != null) {
            // Replace a fragment that might have been placed before!
            CardFragment fragment = CardFragment.newInstance(card);
            getSupportFragmentManager().beginTransaction().replace(R.id.cardContainer, fragment).commit();
        } else {
            Intent intent = new Intent(MainActivity.this, CardActivity.class);
            intent.putExtra(CARD_INTENT_ARG, card);
            startActivity(intent);
        }
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

    /**
     * Change the icon of the sort-button in the ActionBar according to the current sorting order.
     *
     * @param descending Whether the current sorting order is descending or not (ascending).
     */
    private void setSortIcon(boolean descending){
        if (descending){
            sortButton.setIcon(R.drawable.ic_sort_descending_white_48dp);
        } else {
            sortButton.setIcon(R.drawable.ic_sort_ascending_white_48dp);
        }
    }

    /**
     * This method sorts all data that's currently available. When sortedDescending is
     * true, it's sorted from A-Z and otherwise it's sorted from Z-A.
     *
     * This function runs in O(n*log(n))-time
     *
     * @param sortedDescending Sort data ascending or descending
     */
    private void sort(boolean sortedDescending){
        if (sortedDescending) {
            Collections.sort(data, new Comparator<Card>() {
                public int compare(Card store1, Card store2) {
                    return store1.getName().toLowerCase().compareTo(store2.getName().toLowerCase());
                }
            });
        } else {
            Collections.sort(data, new Comparator<Card>() {
                public int compare(Card store1, Card store2) {
                    return store1.getName().toLowerCase().compareTo(store2.getName().toLowerCase());
                }
            });
        }
    }

    /**
     * This function reverses the order of all data that's currently available. Do not resort
     * data just to change the sort preference (descending - ascending) as reversing is
     * faster.
     *
     * This function runs in O(n)-time
     */
    private void reverse(){
        //Runs in O(n) time. We can do this because all data is sorted alphabetically by default
        Collections.reverse(data);
        overviewFragment.refreshData(data);
    }
}
