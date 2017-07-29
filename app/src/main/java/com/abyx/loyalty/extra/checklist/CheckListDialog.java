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

package com.abyx.loyalty.extra.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.abyx.loyalty.R;

import java.util.List;

/**
 * Dialog that contains a list with checkable items. The items that were chosen by the user to be
 * selected can be retrieved.
 *
 * @author Pieter Verschaffelt
 */
public class CheckListDialog<T> extends AlertDialog {
    private RecyclerView checkList;
    private CheckListAdapter<T> checkListAdapter;
    private List<T> data;
    private CheckableContentProvider<T> provider;
    private CheckListAdapter<T> adapter;
    private CheckListListener<T> listener;

    public CheckListDialog(List<T> data, CheckableContentProvider<T> provider, CheckListListener<T> listener, Context context) {
        super(context);
        this.data = data;
        this.provider = provider;
        this.listener = listener;
    }

    public CheckListDialog(List<T> data, CheckableContentProvider<T> provider, CheckListListener<T> listener, Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.data = data;
        this.provider = provider;
        this.listener = listener;
    }

    public CheckListDialog(List<T> data, CheckableContentProvider<T> provider, CheckListListener<T> listener, Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.data = data;
        this.provider = provider;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_list);
        checkList = (RecyclerView) findViewById(R.id.checkList);

        adapter = new CheckListAdapter<>(data, provider, getContext());
        checkList.setAdapter(adapter);
        checkList.setLayoutManager(new LinearLayoutManager(getContext()));

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        // Close the CheckListDialog when cancel button is pressed.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.selected(adapter.getSelectedItems());
                dismiss();
            }
        });
    }


}
