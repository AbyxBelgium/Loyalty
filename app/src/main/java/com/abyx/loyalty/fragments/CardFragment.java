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

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
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
import com.abyx.loyalty.graphics.BarcodeGenerator;
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

import be.abyx.aurora.AuroraFactory;
import be.abyx.aurora.BlurryAurora;
import be.abyx.aurora.CircleShape;
import be.abyx.aurora.DefaultAuroraFactory;
import be.abyx.aurora.ParallelAuroraFactory;
import be.abyx.aurora.ImageUtils;
import be.abyx.aurora.ParallelShapeFactory;
import be.abyx.aurora.RectangleShape;
import be.abyx.aurora.ShapeFactory;

/**
 * This fragment shows all details for one loyalty card (this includes the barcode and a small logo)
 *
 * @author Pieter Verschaffelt
 */
public class CardFragment extends Fragment implements ProgressIndicator, APIConnectorCallback {
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
        // First of all we switch the currently used theme!
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.DetailedTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        View view = localInflater.inflate(R.layout.fragment_card, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        }

        // Now, we look for all views that need to be initialized with a specific value
        barcodeImage = (ImageView) view.findViewById(R.id.barcodeImage);
        logoView = (ImageView) view.findViewById(R.id.logoView);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        rootView = view.findViewById(R.id.rootLayout);

        long id = getArguments().getLong(Constants.INTENT_CARD_ID_ARG);
        Database db = new Database(getActivity());
        db.openDatabase();
        data = db.getCardByID(id);
        db.closeDatabase();
        if (data != null) {
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
        return view;
    }

    /**
     * This function returns a bitmap that's generated using the given barcode (as a string) and
     * it's format.
     * TODO: this function should be executed asynchronously in a separate thread to avoid jank.
     * TODO: proper error handling should be implemented for this function.
     *
     * @param barcode The value of the barcode that has to be processed into a bitmap
     * @param format The format of the barcode that has to be processed into a bitmap
     * @return A bitmap representing the given barcode and it's format
     */
    public Bitmap encodeAsBitmap(String barcode, BarcodeFormat format) {
        BarcodeGenerator generator = new BarcodeGenerator(getContext());
        try {
            return generator.renderBarcode(barcode, format, 300, 50);
        } catch (WriterException e) {
            // TODO implement error handling here!
            System.err.println(e);
        }

        return null;
    }

    @Override
    public void setDone(boolean done) {
        if (done){
            progress.setVisibility(View.INVISIBLE);
            // Also change the background of the app
            AuroraFactory factory = new ParallelAuroraFactory(this.getContext());

            BitmapDrawable drawable = (BitmapDrawable) this.logoView.getDrawable();
            Bitmap logo = drawable.getBitmap();
            ImageUtils utils = new ImageUtils(getContext());
            Bitmap croppedLogo = utils.magicCrop(logo, Color.WHITE, 0.2f);
            ShapeFactory shapeFactory = new ParallelShapeFactory();
            Bitmap circle = shapeFactory.createShape(new CircleShape(getContext()), croppedLogo, Color.argb(143, 175, 175, 175), 150);
            BitmapDrawable newLogo = new BitmapDrawable(getResources(), circle);
            this.logoView.setImageDrawable(newLogo);

            Bitmap aurora = factory.createAuroraBasedUponDrawable(logo, new BlurryAurora(this.getContext()), 1080, 1920);

            this.getActivity().findViewById(R.id.rootLayout).setBackground(new BitmapDrawable(getResources(), aurora));
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
        getActivity().setTitle(data.getName());
    }

    /**
     * @return The card that's currently been displayed in this fragment.
     */
    public Card getCard() {
        return data;
    }
}
