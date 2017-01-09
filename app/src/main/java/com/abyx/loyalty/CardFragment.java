package com.abyx.loyalty;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * This fragment shows all details for one loyalty card (this includes the barcode and a small logo)
 *
 * @author Pieter Verschaffelt
 */
public class CardFragment extends Fragment implements ProgressIndicator {
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
        barcodeImage = (ImageView) view.findViewById(R.id.barcodeImage);
        logoView = (ImageView) view.findViewById(R.id.logoView);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        if (getArguments() != null) {
            data = getArguments().getParcelable("CARD");
            if (data != null) {
                barcodeView.setText(data.getBarcode());
                barcodeImage.setImageBitmap(encodeAsBitmap(data.getBarcode(), data.getFormat()));
                DownloadImageTask temp = new DownloadImageTask(logoView, getActivity(), data.getImageLocation(), data);
                temp.setProgressIndicator(this);
                temp.execute(data.getImageURL());
                barcodeView.setText(data.getBarcode());
                getActivity().setTitle(data.getName());
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
}
