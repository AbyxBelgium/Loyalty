package com.abyx.loyalty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;


public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText storeName;
    private EditText logoURL;
    private EditText barcode;
    private Spinner formatSpinner;

    private Card data;
    private String format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        storeName = (EditText) findViewById(R.id.storeName);
        logoURL = (EditText) findViewById(R.id.logoURL);
        barcode = (EditText) findViewById(R.id.barcode);
        formatSpinner = (Spinner) findViewById(R.id.formatSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.format_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        formatSpinner.setAdapter(adapter);
        data = getIntent().getParcelableExtra("DATA");
        formatSpinner.setSelection(adapter.getPosition(data.getFormat().toString()));
        format = data.getFormat().toString();
        storeName.setText(data.getName());
        logoURL.setText(data.getImageURL());
        barcode.setText(data.getBarcode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            //Check whether storename field is filled
            if (storeName.getText().toString().equals("")){
                storeName.setError(getString(R.string.empty_store_name));
                return true;
            }

            if (Utils.isValidBarcode(barcode.getText().toString(), BarcodeFormat.valueOf(format))) {
                Intent temp = new Intent();
                data.setName(storeName.getText().toString());
                data.setImageLocation(logoURL.getText().toString());
                data.setBarcode(barcode.getText().toString());
                data.setFormat(BarcodeFormat.valueOf(format));
                temp.putExtra("DATA", data);
                setResult(RESULT_OK, temp);
                finish();
            } else {
                barcode.setError(getString(R.string.wrong_barcode_input));
            }
            return true;
        } else {
            Intent temp = new Intent();
            temp.putExtra("DATA", data);
            setResult(RESULT_OK, temp);
            finish();
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        format = (String) parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("DATA", data);
        setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }
}
