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

package com.abyx.loyalty.extra;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abyx.loyalty.R;
import com.abyx.loyalty.tasks.OverviewLogoManager;
import com.abyx.loyalty.contents.Card;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CardAdapter extends BaseAdapter {
    private Context context;
    private List<Card> data;
    private ThreadPoolExecutor executor;

    public CardAdapter(Context context, List<Card> data){
        this.context = context;
        this.data = data;

        this.executor = new ThreadPoolExecutor(8, 20, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Card getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        System.out.println("Refresh view " + i + "!");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.single_grid, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.textView);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        textView.setText(data.get(i).getName());
        OverviewLogoManager logoManager = new OverviewLogoManager(context, imageView, data.get(i), executor, view);
        logoManager.start();
        return view;
    }

    public void refresh(List<Card> items) {
        this.data = items;
        notifyDataSetChanged();
    }
}
