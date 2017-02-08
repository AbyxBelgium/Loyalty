package com.abyx.loyalty.fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.exceptions.InvalidCardException;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.Utils;
import com.abyx.loyalty.tasks.APIConnectorCallback;
import com.abyx.loyalty.tasks.APIConnectorTask;
import com.abyx.loyalty.tasks.DownloadImageTask;
import com.abyx.loyalty.extra.ProgressIndicator;
import com.abyx.loyalty.R;
import com.abyx.loyalty.tasks.ThumbnailDownloader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.List;

/**
 * This fragment shows all details for one loyalty card (this includes the barcode and a small logo)
 *
 * @author Pieter Verschaffelt
 */
public class CardFragment extends Fragment implements ProgressIndicator, APIConnectorCallback {
    private TextView barcodeView;
    private ImageView barcodeImage;
    private ImageView logoView;
    private ProgressBar progress;
    private View rootView;

    private Card data;

    public CardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @return A new instance of fragment CardFragment.
     */
    public static CardFragment newInstance(long cardID) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.INTENT_CARD_ID_ARG, cardID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Need to pass false here to be able to add the fragment programmatically in an activity later
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        barcodeView = (TextView) view.findViewById(R.id.barcodeView);
        barcodeImage = (ImageView) view.findViewById(R.id.barcodeImage);
        logoView = (ImageView) view.findViewById(R.id.logoView);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        rootView = view.findViewById(R.id.rootLayout);

        if (getArguments() != null) {
            long id = getArguments().getLong(Constants.INTENT_CARD_ID_ARG);
            Database db = new Database(getActivity());
            db.openDatabase();
            data = db.getCardByID(id);
            db.closeDatabase();
            if (data != null) {
                barcodeView.setText(data.getBarcode());
                getActivity().setTitle(data.getName());

                // Resource URL for the logo can be changed when user long presses the current logo
                logoView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final EditText editText = new EditText(getActivity());
                        builder.setView(editText);
                        builder.setTitle(R.string.change_logo);
                        builder.setMessage(R.string.enter_url_message);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (editText.getText().toString().equals("")) {
                                    data.setDefaultImageLocation();
                                } else {
                                    data.setImageLocation(editText.getText().toString());
                                }
                                new DownloadImageTask(logoView, getActivity(), data.getImageLocation(), data).execute(data.getImageURL());
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();

                        dialog.show();
                        return true;
                    }
                });

                if (data.getImageURL().contains("Stack.png") || data.getImageURL().equals("")) {
                    progress.setVisibility(View.VISIBLE);
                    APIConnectorTask connectorTask = new APIConnectorTask(this, getActivity());
                    connectorTask.execute(data.getName());
                } else {
                    data = new Card(data.getName(), data.getBarcode(), data.getImageURL(), data.getFormat());
                    initGui(data);
                }
            }
        }
        return view;
    }

    /**
     * This function returns a bitmap that's generated using the given barcode (as a string) and
     * it's format.
     * TODO: this function should be executed asynchronously in a seperate thread to avoid jank
     *
     * @param barcode The value of the barcode that has to be processed into a bitmap
     * @param format The format of the barcode that has to be processed into a bitmap
     * @return A bitmap representing the given barcode and it's format
     */
    public Bitmap encodeAsBitmap(String barcode, BarcodeFormat format){
        long start = System.currentTimeMillis();
        Writer barWriter = new MultiFormatWriter();
        int width = 700;
        int height = 300;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        try {
            BitMatrix bm = barWriter.encode(barcode, format, width, height);
            for (int j = 0; j < height; j++) {
                int[] row = new int[width];
                for (int i = 0; i < width; i++) {
                    row[i] = bm.get(i, j) ? Color.BLACK : Color.WHITE;
                }
                //We use setPixels to set the pixels of a whole row at once to increase performance
                bitmap.setPixels(row, 0, width, 0, j, width, 1);
            }
        } catch (WriterException e){
            System.err.println("An error occured: " + e);
        }
        return bitmap;
    }

    @Override
    public void setDone(boolean done) {
        if (done){
            progress.setVisibility(View.INVISIBLE);
        } else {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setProgress(double percentage) {
        // Nothing to do here!
    }

    @Override
    public void onAPIReady(String url){
        if (data != null) {
            makeImageURLPersistent(data, url);
            initGui(data);
        }
    }

    @Override
    public void onAPIException(String title, String message){
        Utils.showInformationDialog(title, message, getActivity(), Utils.createDismissListener());
        if (data != null) {
            initGui(data);
        }
    }

    private void makeImageURLPersistent(Card card, String url) {
        card.setImageLocation(url);
        Database db = new Database(getActivity());
        db.openDatabase();
        try {
            db.updateCard(card);
        } catch (InvalidCardException e) {
            Snackbar.make(rootView, getString(R.string.invalid_card_exception), Snackbar.LENGTH_LONG);
        }
    }

    private void initGui(final Card data) {
        DownloadImageTask tempDownloader = new DownloadImageTask(logoView, getActivity(), data.getImageLocation(), data, true);
        tempDownloader.setProgressIndicator(this);
        tempDownloader.execute(data.getImageURL());
        new ThumbnailDownloader(getActivity(), data.getImageLocation(), data).execute(data.getImageURL());
        barcodeImage.setImageBitmap(encodeAsBitmap(data.getBarcode(), data.getFormat()));
        barcodeView.setText(data.getBarcode());
        getActivity().setTitle(data.getName());
    }

    /**
     * @return The card that's currently been displayed in this fragment.
     */
    public Card getCard() {
        return data;
    }
}
