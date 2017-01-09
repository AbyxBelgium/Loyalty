package com.abyx.loyalty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;

public class ManualInputActivity extends AppCompatActivity {
    private EditText barcodeText;
    private Spinner formatSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
        barcodeText = (EditText) findViewById(R.id.barcodeText);
        formatSpinner = (Spinner) findViewById(R.id.formatSpinner);
        System.out.println("Formatspinner: " + formatSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.format_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        formatSpinner.setAdapter(adapter);
        formatSpinner.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_input, menu);
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
            if (barcodeText.getText().equals("")){
                barcodeText.setError(getString(R.string.wrong_barcode_input));
            } else {
                if (Utils.isValidBarcode(barcodeText.getText().toString(), BarcodeFormat.valueOf(formatSpinner.getSelectedItem().toString()))){
                    Intent intent = new Intent(ManualInputActivity.this, FinishActivity.class);
                    Intent created = getIntent();
                    intent.putExtra("BARCODE", barcodeText.getText().toString());
                    intent.putExtra("FORMAT", formatSpinner.getSelectedItem().toString());
                    intent.putExtra("STORENAME", created.getStringExtra("STORENAME"));
                    startActivityForResult(intent, Utils.ADD_STORE);
                } else {
                    barcodeText.setError(getString(R.string.wrong_barcode_input));
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
