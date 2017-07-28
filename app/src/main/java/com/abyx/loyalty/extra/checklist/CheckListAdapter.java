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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.abyx.loyalty.R;

import java.util.List;

/**
 * The CheckListAdapter is used for controlling the CheckList.
 *
 * @author Pieter Verschaffelt
 */
public class CheckListAdapter<T> extends RecyclerView.Adapter<CheckListAdapter.CheckListViewHolder> {
    private Context context;
    private List<T> data;
    private CheckableContentProvider<T> dataProvider;

    public class CheckListViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView nameTextView;

        public CheckListViewHolder(View itemView) {
            super(itemView);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            this.nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        }
    }

    public CheckListAdapter(List<T> data, CheckableContentProvider<T> dataProvider, Context context) {
        this.context = context;
        this.data = data;
        this.dataProvider = dataProvider;
    }

    @Override
    public CheckListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CheckListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
