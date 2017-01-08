package com.abyx.loyalty;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This activity represents a "detailed" screen. Whenever the user taps on any store, this
 * activity opens up showing them the barcode and enlarged store logo. There's also an edit
 * button available to change some of the entered information.
 */
public class DetailsActivity extends DetailedActivity implements ProgressIndicator {
    private ArrayList<Card> list;
    private Card data;
    private Card temp;
    private TextView barcodeView;
    private ImageView barcodeImage;
    private ImageView logoView;
    private ProgressBar progress;
    private int REQUEST_EDIT = 1;
    private NfcAdapter nfcAdapter;
    private boolean nfcAvailable = false;
    private Uri[] uris = new Uri[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_card);
        barcodeImage = (ImageView) findViewById(R.id.barcodeImage);
        barcodeView = (TextView) findViewById(R.id.barcodeView);
        logoView = (ImageView) findViewById(R.id.logoView);
        progress = (ProgressBar) findViewById(R.id.progress);
        intent = getIntent();
        if (list == null) {
            list = intent.getParcelableArrayListExtra("LIST");
            data = list.get(intent.getIntExtra("POS", 0));
        }
        initializeUI();

        //TODO fix NFC here!
        //Check whether NFC and Android Beam are supported on this device
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1 && getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)){
//            nfcAvailable = true;
//            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//            try {
//                IO temp = new IO(this);
//                uris[0] = temp.copyToExternalStorage(data);
//                nfcAdapter.setBeamPushUrisCallback(new FileUriCallback(), this);
//            } catch (MakeDirException e){
//                Utils.showInformationDialog(getString(R.string.create_dir_error_title),
//                        getString(R.string.create_dir_error_message), this, Utils.createDismissListener());
//            } catch (IOException e){
//                Utils.showInformationDialog(getString(R.string.unexpected_io_error_title),
//                        getString(R.string.unexpected_io_error_message), this, Utils.createDismissListener());
//            }
//        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //Delete cache file
        new IO(this).clearCache();
    }

    private void initializeUI(){
        long start = System.currentTimeMillis();
        barcodeImage.setImageBitmap(encodeAsBitmap(data.getBarcode(), data.getFormat()));
        System.out.println("Set bitmap: " + (System.currentTimeMillis() - start) + "ms");
        DownloadImageTask temp = new DownloadImageTask(logoView, this, data.getImageLocation(), data);
        System.out.println("Create DownloadImageTask: " + (System.currentTimeMillis() - start) + "ms");
        temp.setProgressIndicator(this);
        System.out.println("Set progressindicator: " + (System.currentTimeMillis() - start) + "ms");
        temp.execute(data.getImageURL());
        System.out.println("Execute task: " + (System.currentTimeMillis() - start) + "ms");
        barcodeView.setText(data.getBarcode());
        System.out.println("Set barcode text: " + (System.currentTimeMillis() - start) + "ms");
        setTitle(data.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        if (!nfcAvailable){
            //Disable nfc button when this is not available on this device
            menu.findItem(R.id.action_nfc).setVisible(false);
        }
        return true;
    }

    @Override
    @TargetApi(16)
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
            intent.putExtra("DATA", data);
            startActivityForResult(intent, REQUEST_EDIT);
            return true;
        } else if (id == R.id.action_nfc){
            Utils.showInformationDialog(getString(R.string.nfc_support_detected_title),
                    getString(R.string.nfc_support_detected_message), this, Utils.createDismissListener());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent response){
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            //Data was successfully edited
            temp = response.getParcelableExtra("DATA");
        } else if (resultCode == RESULT_CANCELED) {
            data = response.getParcelableExtra("DATA");
        }
    }

    @Override
    public void onResume(){
        if (temp != null){
            data.setName(temp.getName());
            data.setBarcode(temp.getBarcode());
            data.setImageLocation(temp.getImageURL());
            final Context context = getApplicationContext();
            requestWritePermissions(DetailsActivity.this, new ReceivedPermission() {
                @Override
                public void onPermissionGranted() {
                    IO saver = new IO(context);
                    saver.save(list);
                }
            });
        }

        initializeUI();

        super.onResume();
    }

    protected Card getStoreData(){
        return intent.getParcelableExtra("DATA");
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
    }

    @TargetApi(16)
    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {
            //empty constructor
        }

        /**
         * Create content URIs as needed to share with another device
         */
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return uris;
        }
    }


}
