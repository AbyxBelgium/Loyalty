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
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.abyx.loyalty.R;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The CheckListAdapter is used for controlling the CheckList.
 *
 * @author Pieter Verschaffelt
 */
public class CheckListAdapter<T> extends RecyclerView.Adapter<CheckListAdapter<T>.CheckListViewHolder> {
    private Context context;
    private List<T> data;
    private Set<T> selectedItems;
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
        this.selectedItems = new HashSet<>();
    }

    @Override
    public CheckListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_checklist, parent, false);
        return new CheckListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CheckListAdapter<T>.CheckListViewHolder holder, int position) {
        T currentObject = data.get(position);
        holder.nameTextView.setText(dataProvider.getCheckableContent(currentObject));
        if (selectedItems.contains(currentObject)) {
            holder.checkBox.setSelected(true);
        } else {
            holder.checkBox.setSelected(false);
        }

        if (dataProvider.isActivated(currentObject)) {
            holder.nameTextView.setTextColor(Color.RED);
        } else {
            holder.nameTextView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public Collection<T> getSelectedItems() {
        return this.selectedItems;
    }
}
