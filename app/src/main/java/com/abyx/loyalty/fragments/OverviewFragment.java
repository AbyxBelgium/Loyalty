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

package com.abyx.loyalty.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.extra.CardAdapter;
import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.RecyclerTouchListener;
import com.abyx.loyalty.extra.recycler.BaseAdapter;
import com.abyx.loyalty.extra.recycler.MultiMode;
import com.abyx.loyalty.managers.DrawableManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment provides an overview of all loyalty cards stored on this device. These cards are
 * shown in a grid.
 *
 * @author Pieter Verschaffelt
 */
public class OverviewFragment extends ListFragment<Card> {
    private RecyclerView mainList;
    private RelativeLayout placeholder;
    private TextView placeholderText;

    private List<Card> data =  new ArrayList<>();
    private ListInteractor<Card> listener;

    private CardAdapter adapter;

    // Whether this fragment is visible or not
    private boolean visible = false;

    private boolean hasFiltered = false;

    // Do not filter anything by default
    private Filter<Card> filter = new Filter<Card>() {
        @Override
        public boolean retain(Card item) {
            return true;
        }
    };

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment.
     *
     * @return A new instance of fragment OverviewFragment.
     */
    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        visible = true;

        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        this.placeholder = (RelativeLayout) view.findViewById(R.id.placeholder);
        this.placeholderText = (TextView) view.findViewById(R.id.placeholderText);

        this.data = new ArrayList<>(listener.requestData());

        changePlaceholderVisibility(data);

        mainList = (RecyclerView) view.findViewById(R.id.mainList);

        DrawableManager drawableManager = new DrawableManager();

        MultiMode mode = new MultiMode.Builder(listener.getToolbar(), getActivity())
                .setMenu(R.menu.menu_contextual, new MultiMode.Callback() {
                    @Override
                    public boolean onMenuItemClick(BaseAdapter adapter, MenuItem item) {
                        int itemId = item.getItemId();

                        //noinspection SimplifiableIfStatement
                        if (itemId == R.id.action_removeSelected) {
                            int[] checked = adapter.getAllChecked(false);
                            List<Card> toRemove = new ArrayList<Card>();
                            for (Integer check: checked) {
                                toRemove.add(data.get(check));
                            }
                            listener.removeCards(toRemove);
                            return true;
                        }

                        return false;
                    }
                })
                .setOriginalMenu(R.menu.menu_main, listener.getOptionsMenuCallback())
                .setNavigationIcon(drawableManager.getDrawable(getContext(), null, R.drawable.ic_arrow_back_white_24dp))
                .build();

        adapter = new CardAdapter(data, getContext(), mode, false, listener);

        adapter.setClickListener(new RecyclerTouchListener() {
            @Override
            public void onClickItem(View v, int position) {
                Card c = data.get(position);
                listener.onItemClick(position, c);
            }
        });

        mainList.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mainList.setLayoutManager(llm);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListInteractor) {
            listener = (ListInteractor<Card>) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement the " +
                    "OverviewFragmentInteractionListener interface!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void dataChanged(List<Card> updated) {
        this.data.clear();
        this.data.addAll(updated);

        if (!visible) {
            return;
        }

        filter();
        changePlaceholderVisibility(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void filter(Filter<Card> filter) {
        this.filter = filter;

        if (!visible) {
            return;
        }

        this.data.clear();
        this.data.addAll(listener.requestData());

        filter();
        changePlaceholderVisibility(data);
        adapter.notifyDataSetChanged();
    }

    private void filter() {
        List<Card> toRemove = new ArrayList<>();
        for (Card card: this.data) {
            if (!filter.retain(card)) {
                toRemove.add(card);
            }
        }
        this.data.removeAll(toRemove);
        hasFiltered = toRemove.size() != 0;
    }

    private void changePlaceholderVisibility(List<Card> data) {
        if (data.size() != 0) {
            this.placeholder.setVisibility(View.GONE);
        } else {
            // Change placeholder text and image when no results where found due to filtering or
            // due to an empty library.
            if (hasFiltered) {
                this.placeholderText.setText(getString(R.string.no_cards_found));
            } else {
                this.placeholderText.setText(getString(R.string.no_cards_yet));
            }
            this.placeholder.setVisibility(View.VISIBLE);
        }
    }
}
