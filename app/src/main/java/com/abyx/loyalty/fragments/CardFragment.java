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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abyx.loyalty.activities.MainActivity;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.exceptions.InvalidCardException;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.Utils;
import com.abyx.loyalty.graphics.BarcodeGenerator;
import com.abyx.loyalty.managers.DrawableManager;
import com.abyx.loyalty.tasks.AuroraTask;
import com.abyx.loyalty.tasks.BarcodeTask;
import com.abyx.loyalty.tasks.DetailedLogoTask;
import com.abyx.loyalty.extra.ProgressIndicator;
import com.abyx.loyalty.R;
import com.abyx.loyalty.tasks.LogoTask;
import com.abyx.loyalty.tasks.TaskListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import be.abyx.aurora.AuroraFactory;
import be.abyx.aurora.BlurryAurora;
import be.abyx.aurora.CircleShape;
import be.abyx.aurora.ParallelAuroraFactory;
import be.abyx.aurora.ImageUtils;
import be.abyx.aurora.ParallelShapeFactory;
import be.abyx.aurora.ShapeFactory;

/**
 * This fragment shows all details for one loyalty card (this includes the barcode and a small logo)
 *
 * @author Pieter Verschaffelt
 */
public class CardFragment extends Fragment {
    private ImageView barcodeImage;
    private ImageView logoView;
    private ProgressBar progress;
    private View rootView;

    private ThreadPoolExecutor poolExecutor;

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

        this.poolExecutor = new ThreadPoolExecutor(4, 8, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

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
                                data.setImageURL(editText.getText().toString());
                            }
                            // TODO FIX THIS
                            //new DownloadImageTask(logoView, getActivity(), data.getImageLocation(), data).execute(data.getImageURL());
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

            progress.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(data.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        LogoTask task = new LogoTask(this.getContext(), new LogoTaskListener());
        task.executeOnExecutor(poolExecutor, getCard());
    }

    private class LogoTaskListener implements TaskListener<Bitmap> {
        @Override
        public void onProgressUpdate(double progress) {
            // Nothing to do here!
        }

        @Override
        public void onFailed(Throwable exception) {
            showNonFatalIOError();

            BarcodeTask barcodeTask = new BarcodeTask(getContext(), new BarcodeTaskListener(), getCard());
            barcodeTask.executeOnExecutor(poolExecutor);

            DrawableManager drawableManager = new DrawableManager();

            AuroraTask auroraTask = new AuroraTask(getContext(), new AuroraTaskListener(), getCard());
            auroraTask.executeOnExecutor(poolExecutor, drawableManager.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_error_outline_black_24dp));
        }

        @Override
        public void onDone(Bitmap result) {
            DetailedLogoTask task = new DetailedLogoTask(getContext(), new DetailedTaskListener(), getCard());
            task.executeOnExecutor(poolExecutor, result);

            BarcodeTask barcodeTask = new BarcodeTask(getContext(), new BarcodeTaskListener(), getCard());
            barcodeTask.executeOnExecutor(poolExecutor);

            AuroraTask auroraTask = new AuroraTask(getContext(), new AuroraTaskListener(), getCard());
            auroraTask.executeOnExecutor(poolExecutor, result);
        }
    }

    private class DetailedTaskListener implements TaskListener<Bitmap> {
        @Override
        public void onProgressUpdate(double progressValue) {
            if (progressValue == 1.0) {
                hideProgressBar();
            }
        }

        @Override
        public void onFailed(Throwable exception) {
            showNonFatalIOError();
        }

        @Override
        public void onDone(Bitmap result) {
            if (isAdded()) {
                logoView.setImageDrawable(new BitmapDrawable(getResources(), result));
            }
        }
    }

    private class BarcodeTaskListener implements TaskListener<Bitmap> {
        @Override
        public void onProgressUpdate(double progress) {

        }

        @Override
        public void onFailed(Throwable exception) {
            showIOErrorDialog();
        }

        @Override
        public void onDone(Bitmap result) {
            if (isAdded()) {
                barcodeImage.setImageDrawable(new BitmapDrawable(getResources(), result));
            }
        }
    }

    private class AuroraTaskListener implements TaskListener<Bitmap> {
        @Override
        public void onProgressUpdate(double progress) {

        }

        @Override
        public void onFailed(Throwable exception) {
            Utils.showToast(getString(R.string.unexpected_io_error), Toast.LENGTH_LONG, getContext());

        }

        @Override
        public void onDone(final Bitmap result) {
            if (isAdded()) {
                TransitionDrawable transitionDrawable = buildTransitionDrawable(result);
                getActivity().findViewById(R.id.rootLayout).setBackground(transitionDrawable);
                transitionDrawable.startTransition(350);
            }
        }

        private TransitionDrawable buildTransitionDrawable(final Bitmap result) {
            Drawable[] layers = new Drawable[2];
            DrawableManager drawableManager = new DrawableManager();
            layers[0] = drawableManager.getDrawable(getContext(), getActivity().getTheme(), R.drawable.bg);
            layers[1] = new BitmapDrawable(getResources(), result);

            return new TransitionDrawable(layers);
        }
    }

    /**
     * @return The card that's currently been displayed in this fragment.
     */
    public Card getCard() {
        return data;
    }

    /**
     * Hide the ProgressBar and make the logo visible using an animation.
     */
    private void hideProgressBar() {
        if (!isAdded()) {
            return;
        }

        logoView.setAlpha(0.0f);
        logoView.setVisibility(View.VISIBLE);

        int shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        logoView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        progress.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progress.setVisibility(View.GONE);
                    }
                });
    }

    private void showNonFatalIOError() {
        Utils.showToast(getString(R.string.unexpected_io_error), Toast.LENGTH_LONG, getContext());
        DrawableManager drawableManager = new DrawableManager();
        Drawable error = drawableManager.getDrawable(getContext(), getActivity().getTheme(), R.drawable.ic_error_outline_black_24dp);
        logoView.setImageDrawable(error);
        hideProgressBar();
    }

    /**
     * Show an error dialog that informs the user that an IO error has occurred and close the
     * activity (go back to the main menu) when the user clicks OK.
     */
    private void showIOErrorDialog() {
        Utils.showInformationDialog(getString(R.string.unexpected_error), getString(R.string.unexpected_io_error), getContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go back to main menu
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
