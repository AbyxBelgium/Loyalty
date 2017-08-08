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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.recycler.BaseAdapter;
import com.abyx.loyalty.extra.recycler.MultiMode;
import com.abyx.loyalty.fragments.ListInteractor;
import com.abyx.loyalty.managers.cache.CacheManager;
import com.abyx.loyalty.managers.DebugManager;
import com.abyx.loyalty.managers.DrawableManager;
import com.abyx.loyalty.managers.OverviewLogoManager;
import com.abyx.loyalty.managers.cache.OverviewCache;
import com.abyx.loyalty.managers.cache.RawCache;
import com.abyx.loyalty.managers.memory.HighMemoryGovernor;
import com.abyx.loyalty.managers.memory.LowMemoryGovernor;
import com.abyx.loyalty.managers.memory.MemoryGovernor;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the RecyclerView.Adapter-interface that's used for displaying information about
 * individual Loyalty cards as a summary.
 *
 * @author Pieter Verschaffelt
 */
public class CardAdapter extends BaseAdapter<CardAdapter.CardViewHolder> {
    private List<Card> cards;
    private Context context;

    private ThreadPoolExecutor executor;
    private RecyclerTouchListener clickListener;

    private ListInteractor<Card> removeListener;

    public class CardViewHolder extends BaseAdapter.BaseViewHolder {
        public TextView textView;
        public ImageView imageView;
        private LinearLayout rootLayout;

        private Drawable whiteBackground;
        private Drawable blackBackground;

        public CardViewHolder(View itemView) {
            super(itemView, clickListener);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayout);

            DrawableManager drawableManager = new DrawableManager();
            whiteBackground = drawableManager.getDrawable(context, null, R.color.white);
            blackBackground = drawableManager.getDrawable(context, null, R.color.bg_home);
        }

        @Override
        public void updateBackground() {
            if (isChecked(getAdapterPosition())) {
                // Set an appropriate background for the selected item
                textView.setTextColor(Color.BLACK);
                rootLayout.setBackground(whiteBackground);
            } else {
                // Set default background for the selected item
                textView.setTextColor(Color.WHITE);
                rootLayout.setBackground(blackBackground);
            }
        }

        @Override
        public void onBindData() {
            determineState();
        }
    }

    public CardAdapter(List<Card> cards, Context context, MultiMode multiMode, boolean animate, ListInteractor<Card> removeListener) {
        super(multiMode, animate);
        this.cards = cards;
        this.context = context;
        this.removeListener = removeListener;

        MemoryGovernor memoryGovernor = getMemoryGovernor();
        DebugManager.debugPrint("Rendering up to " + memoryGovernor.concurrentTasks() + " cards at the same time.", context);

        this.executor = new ThreadPoolExecutor(Math.min(memoryGovernor.concurrentTasks(), 4), memoryGovernor.concurrentTasks(), 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    }

    public void setClickListener(RecyclerTouchListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Card c = this.cards.get(position);
        holder.textView.setText(c.getName());
        holder.imageView.setImageDrawable(null);

        // Start task to set correct image in ImageView
        OverviewLogoManager manager = new OverviewLogoManager(context, holder.imageView, c, executor);
        manager.start();
        holder.onBindData();
    }

    @Override
    public void removeAt(int index) {

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

    private MemoryGovernor getMemoryGovernor() {
        CacheManager cacheManager = new CacheManager(context);

        // This check will determine if the logo for a card has been found before. If that's the case
        // we can return a less memory hungry MemoryGovernor. We only check if the first card is
        // present for efficiency purposes.
        if (this.cards != null && this.cards.size() >= 1 && !cacheManager.inCache(this.cards.get(0), new RawCache())) {
            return new HighMemoryGovernor();
        } else {
            return new LowMemoryGovernor();
        }
    }
}
