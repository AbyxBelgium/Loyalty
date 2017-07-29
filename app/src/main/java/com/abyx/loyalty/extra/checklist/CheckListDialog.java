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

    public CheckListDialog(List<T> data, CheckableContentProvider<T> provider, Context context) {
        super(context);
        this.data = data;
        this.provider = provider;
    }

    public CheckListDialog(List<T> data, CheckableContentProvider<T> provider, Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.data = data;
        this.provider = provider;
    }

    public CheckListDialog(List<T> data, CheckableContentProvider<T> provider, Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.data = data;
        this.provider = provider;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_list);
        checkList = (RecyclerView) findViewById(R.id.checkList);

        adapter = new CheckListAdapter<>(data, provider, getContext());
        checkList.setAdapter(adapter);
        checkList.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
