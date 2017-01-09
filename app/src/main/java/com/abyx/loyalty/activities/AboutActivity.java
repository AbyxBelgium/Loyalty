package com.abyx.loyalty.activities;

import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.abyx.loyalty.R;

/**
 * This activity shows the about screen where the user can find all sorts of developer related
 * information. This includes the app's version number, legal information and so on...
 *
 * @author Pieter Verschaffelt
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //Version number is loaded programmatically because this isn't supported as an operation
        //in the layout files.
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView) findViewById(R.id.versionField)).setText(versionName);
        } catch (PackageManager.NameNotFoundException e){
            //The version number is simply not shown when something goes wrong. No further
            //information has to be given here.
        }
    }
}
