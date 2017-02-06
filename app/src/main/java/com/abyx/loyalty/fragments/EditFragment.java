package com.abyx.loyalty.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
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
}
