package com.abyx.loyalty;

import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This fragment shows all details for one loyalty card (this includes the barcode and a small logo)
 *
 * @author Pieter Verschaffelt
 */
public class CardFragment extends Fragment {
    private static final String CARD_ARG = "CARD";

    private TextView barcodeView;
    private ImageView barcodeImage;
    private ImageView logoView;
    private ProgressBar progress;

    private Card data;

    public CardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CardFragment.
     */
    public static CardFragment newInstance(Card data) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putParcelable("CARD", data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Need to pass false here to be able to add the fragment programmatically in an activity later
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        barcodeView = (TextView) view.findViewById(R.id.barcodeView);
        if (getArguments() != null) {
            data = getArguments().getParcelable("CARD");
            if (data != null) {
                barcodeView.setText(data.getBarcode());
            }
        }
        return view;
    }
}
