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

package com.abyx.loyalty.fragments;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.Utils;
import com.abyx.loyalty.managers.cache.CacheManager;
import com.abyx.loyalty.managers.cache.RawCache;
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
    private Database db;
    private EditListener listener;

    public EditFragment() {

    }

    public static EditFragment newInstance(long cardID) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.INTENT_CARD_ID_ARG, cardID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        db = new Database(getActivity());
        db.openDatabase();
        data = db.getCardByID(args.getLong(Constants.INTENT_CARD_ID_ARG));
        db.closeDatabase();

        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        storeName = (EditText) view.findViewById(R.id.storeName);
        storeName.setText(data.getName());

        logoURL = (EditText) view.findViewById(R.id.logoURL);
        logoURL.setText(data.getImageURL());

        barcode = (EditText) view.findViewById(R.id.barcode);
        barcode.setText(data.getBarcode());

        formatSpinner = (Spinner) view.findViewById(R.id.formatSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.format_array, android.R.layout.simple_spinner_item);
        formatSpinner.setOnItemSelectedListener(this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(adapter);
        formatSpinner.setSelection(adapter.getPosition(data.getFormat().toString()));

        return view;
    }

    public void registerListener(EditListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        data.setFormat(BarcodeFormat.valueOf((String) parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                data.setImageURL(logoURL.getText().toString());
                data.setBarcode(barcode.getText().toString());

                CacheManager manager = new CacheManager(getContext());
                manager.removeFromCache(data);

                // Save changes to database
                db.openDatabase();
                db.updateCard(data);
                db.closeDatabase();
                if (listener != null) {
                    listener.doneEditing();
                } else {
                    throw new RuntimeException("A valid EditListener must be registered!");
                }
                return true;
            } else {
                barcode.setError(getString(R.string.wrong_barcode_input));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface EditListener {
        /**
         * This function is called whenever the user is done editing the loyalty card.
         */
        void doneEditing();
    }
}
