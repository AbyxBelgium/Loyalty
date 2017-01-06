package com.abyx.loyalty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private GridView mainGrid;

    private ArrayList<StoreData> data;
    private GridAdapter adapter;
    private MenuItem sortButton;
    private boolean sortedDescending;
    private final String sortedString = "sorted_descending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainGrid = (GridView) findViewById(R.id.mainGrid);
        data = new ArrayList<>();
        adapter = new GridAdapter(this, data);
        mainGrid.setAdapter(adapter);

        mainGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mainGrid.setMultiChoiceModeListener(new MultiChoiceGridViewListener(data, this));

        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("POS", position);
                intent.putParcelableArrayListExtra("LIST", data);
                startActivity(intent);
            }
        });

        //The sortedDescending value from the last time the user used this app
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sortedDescending = sharedPref.getBoolean(sortedString, true);
        sort(sortedDescending);
        adapter.refresh(data);
        sort(sortedDescending);
    }

    public void addData(View view){
        Intent intent = new Intent(MainActivity.this, AddStoreActivity.class);
        startActivityForResult(intent, Utils.ADD_STORE_SCANNER);
        adapter.refresh(data);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (isPermissionGranted(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            IO temp = new IO(getApplicationContext());
            temp.save(data);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        requestReadPermissions(MainActivity.this, new ReceivedPermission() {
            @Override
            public void onPermissionGranted() {
                IO temp = new IO(MainActivity.this);
                List<StoreData> input = temp.load();
                //Performance already optimized from O(n²) to O(n)
                //TODO improve performance from O(n) to O(1)
                if (input.size() > 0 && !data.contains(input.get(0))) {
                    data.addAll(input);
                }
                adapter.refresh(data);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("Resume...");
        if (isPermissionGranted(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            IO temp = new IO(MainActivity.this);
            List<StoreData> input = temp.load();
            //Performance already optimized from O(n²) to O(n)
            //TODO improve performance from O(n) to O(1)
            if (input.size() > 0 && !data.contains(input.get(0))) {
                data.addAll(input);
            }
            adapter.refresh(data);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent temp = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(temp);
            return true;
        } else if (id == R.id.search) {
            Intent temp = new Intent(MainActivity.this, SearchResultsActivity.class);
            temp.putParcelableArrayListExtra("LIST", data);
            startActivity(temp);
            return true;
        } else if (id == R.id.action_sort){
            reverse();
            sortedDescending = !sortedDescending;
            setSortIcon(sortedDescending);
            adapter.refresh(data);
            //Save the sort preference of the user, so he doesn't has to choose this everytime
            //the app starts
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(sortedString, sortedDescending);
            editor.apply();
            return true;
        } else if (id == R.id.action_backup) {
            Intent temp = new Intent(MainActivity.this, BackupRestoreActivity.class);
            temp.putParcelableArrayListExtra("LIST", data);
            startActivity(temp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
            Collections.sort(data, new Comparator<StoreData>() {
                public int compare(StoreData store1, StoreData store2) {
                    return store1.getName().toLowerCase().compareTo(store2.getName().toLowerCase());
                }
            });
        } else {
            Collections.sort(data, new Comparator<StoreData>() {
                public int compare(StoreData store1, StoreData store2) {
                    return store1.getName().toLowerCase().compareTo(store2.getName().toLowerCase());
                }
            });
        }
        adapter.refresh(data);
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
        adapter.refresh(data);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent response) {
        if (requestCode == Utils.ADD_STORE_SCANNER && resultCode == RESULT_OK) {
            // A new store was added
            data.add((StoreData) response.getParcelableExtra("DATA"));
            sort(sortedDescending);
            final Context context = getApplicationContext();
            requestWritePermissions(MainActivity.this, new ReceivedPermission() {
                @Override
                public void onPermissionGranted() {
                    IO temp = new IO(context);
                    temp.save(data);
                }
            });
        }
    }
}
