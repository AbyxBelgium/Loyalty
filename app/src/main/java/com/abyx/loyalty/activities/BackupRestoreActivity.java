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

package com.abyx.loyalty.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.contents.ExportManager;
import com.abyx.loyalty.exceptions.InvalidImportFile;
import com.abyx.loyalty.exceptions.MakeDirException;
import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.CurrentProgressDialog;
import com.abyx.loyalty.extra.ReceivedPermission;
import com.abyx.loyalty.extra.Utils;
import com.abyx.loyalty.extra.checklist.CheckListDialog;
import com.abyx.loyalty.extra.checklist.CheckListListener;
import com.abyx.loyalty.extra.checklist.CheckableContentProvider;
import com.abyx.loyalty.managers.ChangeListener;
import com.abyx.loyalty.managers.ImportManager;
import com.abyx.loyalty.tasks.TaskListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class BackupRestoreActivity extends PermissionActivity {
    private Intent intent;
    private List<Card> data;
    private static final int READ_REQUEST_CODE = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        Database db = new Database(getApplicationContext());
        db.openDatabase();
        db.subscribe(new ChangeListener<List<Card>>() {
            @Override
            public void change(List<Card> resource) {
                data = resource;
            }
        });
        db.getAllCardsSorted(true);
        db.closeDatabase();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void backup(View view){
        final Context context = BackupRestoreActivity.this;
        requestWritePermissions(BackupRestoreActivity.this, new ReceivedPermission() {
            @Override
            public void onPermissionGranted() {
                ExportManager temp = new ExportManager();
                try {
                    temp.exportContents(data);
                    Utils.showToast(getString(R.string.successful_backup), Toast.LENGTH_SHORT, context);
                } catch (MakeDirException e) {
                    System.out.println(e);
                    //Inform the user that we were unable to create a new directory
                    Utils.showInformationDialog(getString(R.string.create_dir_error_title),
                            getString(R.string.create_dir_error_message), context, Utils.createDismissListener());
                } catch (IOException e) {
                    System.out.println(e);
                    //Inform the user that an unknown IO error occurred
                    Utils.showInformationDialog(getString(R.string.unexpected_io_error_title),
                            getString(R.string.unexpected_io_error_message), context, Utils.createDismissListener());
                }
            }
        });
    }

    public void restore(final View view) {
        requestReadPermissions(BackupRestoreActivity.this, new ReceivedPermission() {
            @Override
            public void onPermissionGranted() {
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                // browser.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show all documents
                intent.setType("*/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    InputStream input = getContentResolver().openInputStream(uri);
                    ExportManager exportManager = new ExportManager();
                    List<Card> data = exportManager.getContents(input);

                    final List<Card> allCards = this.data;

                    CheckListDialog<Card> checkListDialog = new CheckListDialog<Card>(data, new CheckableContentProvider<Card>() {
                        @Override
                        public String getCheckableContent(Card input) {
                            return input.getName();
                        }

                        @Override
                        public boolean isActivated(Card input) {
                            // Check if the given card is already present in the system.
                            for (Card card: allCards) {
                                if (card.getName().toLowerCase().equals(input.getName().toLowerCase())) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }, new CheckListListener<Card>() {
                        @Override
                        public void selected(Collection<Card> selectedItems) {
                            importCards(selectedItems);
                        }
                    }, BackupRestoreActivity.this);
                    checkListDialog.setCanceledOnTouchOutside(false);
                    checkListDialog.setCancelable(true);
                    checkListDialog.show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Utils.showToast(getString(R.string.unexpected_io_error), Toast.LENGTH_LONG, getApplicationContext());
                } catch (IOException | InvalidImportFile e) {
                    // TODO properly handle these exceptions
                    e.printStackTrace();
                }
            }
        }
    }

    public void importCards(Collection<Card> cards) {
        final CurrentProgressDialog progressDialog = new CurrentProgressDialog(BackupRestoreActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        ImportManager importManager = new ImportManager(new TaskListener<Void>() {
            @Override
            public void onProgressUpdate(double progress) {
                progressDialog.setProgress((int) (100 * progress));
            }

            @Override
            public void onFailed(Throwable exception) {
                // TODO handle exceptions
            }

            @Override
            public void onDone(Void result) {
                progressDialog.dismiss();
            }
        }, getApplicationContext());
        importManager.run(cards);
    }
}
