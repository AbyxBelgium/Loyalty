package com.abyx.loyalty;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;


public class FinishActivity extends DetailedActivity implements ProgressIndicator, APIConnectorCallback {
    private StoreData data;
    private TextView barcodeView;
    private ImageView barcodeImage;
    private ImageView logoView;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        barcodeImage = (ImageView) findViewById(R.id.barcodeImage);
        barcodeView = (TextView) findViewById(R.id.barcodeView);
        logoView = (ImageView) findViewById(R.id.logoView);
        progress = (ProgressBar) findViewById(R.id.progress);
        intent = getIntent();
        initStoreData();

        logoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FinishActivity.this);
                final EditText editText = new EditText(FinishActivity.this);
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
                        new DownloadImageTask(logoView, FinishActivity.this, data.getImageLocation(),
                                data).execute(data.getImageURL());
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finish, menu);
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
            Intent temp = new Intent();
            temp.putExtra("DATA", data);
            setResult(RESULT_OK, temp);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setDone(boolean done){
        if (done){
            progress.setVisibility(View.INVISIBLE);
        } else {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setProgress(double percentage){
        //Nothing to do here
        //This is only relevant for progressbars which support percentages
    }

    protected void initStoreData() {
        new APIConnectorTask(this, this).execute(intent.getStringExtra("STORENAME"));
    }

    @Override
    public void onAPIReady(String url){
        data = new StoreData(intent.getStringExtra("STORENAME"),
                intent.getStringExtra("BARCODE"), url, BarcodeFormat.valueOf(intent.getStringExtra("FORMAT")));
        DownloadImageTask tempDownloader = new DownloadImageTask(logoView, this, data.getImageLocation(), data, true);
        tempDownloader.setProgressIndicator(this);
        tempDownloader.execute(data.getImageURL());
        new ThumbnailDownloader(this, data.getImageLocation(), data).execute(data.getImageURL());
        barcodeImage.setImageBitmap(encodeAsBitmap(data.getBarcode(), data.getFormat()));
        barcodeView.setText(data.getBarcode());
        setTitle(data.getName());
    }

    @Override
    public void onAPIException(String title, String message){
        Utils.showInformationDialog(title, message, this, Utils.createDismissListener());
        data = new StoreData(intent.getStringExtra("STORENAME"),
                intent.getStringExtra("BARCODE"), BarcodeFormat.valueOf(intent.getStringExtra("FORMAT")));
        DownloadImageTask tempDownloader = new DownloadImageTask(logoView, this, data.getImageLocation(), data, true);
        tempDownloader.setProgressIndicator(this);
        tempDownloader.execute(data.getImageURL());
        new ThumbnailDownloader(this, data.getImageLocation(), data).execute(data.getImageURL());
        barcodeImage.setImageBitmap(encodeAsBitmap(data.getBarcode(), data.getFormat()));
        barcodeView.setText(data.getBarcode());
        setTitle(data.getName());
    }
}
