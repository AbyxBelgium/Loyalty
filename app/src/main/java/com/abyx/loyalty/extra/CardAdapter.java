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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.fragments.OverviewFragment;
import com.abyx.loyalty.tasks.OverviewLogoManager;
import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the RecyclerView.Adapter-interface that's used for displaying information about
 * individual Loyalty cards as a summary.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<Card> cards;
    private Context context;

    private ThreadPoolExecutor executor;

    public class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public CardViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public CardAdapter(List<Card> cards, Context context) {
        this.cards = cards;
        this.context = context;
        this.executor = new ThreadPoolExecutor(4, 8, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Card c = this.cards.get(position);
        holder.textView.setText(c.getName());
        holder.imageView.setImageDrawable(null);

        // Start task to set correct image in ImageView
        OverviewLogoManager manager = new OverviewLogoManager(context, holder.imageView, c, executor);
        manager.start();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_card, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return this.cards.size();
    }


}
