package com.abyx.loyalty.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.Utils;
import com.google.zxing.BarcodeFormat;

/**
 * The EditFragment can be used to change the properties of a certain loyalty card.
 *
 * @author Pieter Verschaffelt
 */

public class EditFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText storeName;
    private EditText logoURL;
    private EditText barcode;
    private Spinner formatSpinner;

    private Card data;

    public EditFragment() {

    }

    public static EditFragment newInstance(Card data) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.INTENT_CARD_ARG, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        storeName = (EditText) view.findViewById(R.id.storeName);
        logoURL = (EditText) view.findViewById(R.id.logoURL);
        barcode = (EditText) view.findViewById(R.id.barcode);
        formatSpinner = (Spinner) view.findViewById(R.id.formatSpinner);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        data.setFormat(BarcodeFormat.valueOf((String) parent.getItemAtPosition(pos)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Nothing has to be done here
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
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

            if (Utils.isValidBarcode(barcode.getText().toString(), data.getFormat())) {
                data.setName(storeName.getText().toString());
                data.setImageLocation(logoURL.getText().toString());
                data.setBarcode(barcode.getText().toString());
                // Save changes to database
                Database db = new Database(getActivity());
                db.openDatabase();
                db.updateCard(data);
                db.closeDatabase();
                getActivity().finish();
                return true;
            } else {
                barcode.setError(getString(R.string.wrong_barcode_input));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
