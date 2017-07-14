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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.CardAdapter;
import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.RecyclerItemListener;
import com.abyx.loyalty.extra.RecyclerTouchListener;

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

    private List<Card> data =  new ArrayList<>();
    private ListInteractor<Card> listener;

    private CardAdapter adapter;

    // Whether this fragment is visible or not
    private boolean visible = false;

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
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        visible = true;

        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        Bundle args = getArguments();
        this.placeholder = (RelativeLayout) view.findViewById(R.id.placeholder);

        if (args != null) {
            this.data = new ArrayList<>(listener.requestData());

            changePlaceholderVisibility(data);

            mainList = (RecyclerView) view.findViewById(R.id.mainList);

            adapter = new CardAdapter(data, getContext());
            mainList.setAdapter(adapter);

            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            mainList.setLayoutManager(llm);

            mainList.addOnItemTouchListener(new RecyclerItemListener(getContext(), mainList, new RecyclerTouchListener() {
                @Override
                public void onClickItem(View v, int position) {
                    Card c = data.get(position);
                    listener.onItemClick(position, c);
                }
            }));
        }

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
    }

    private void changePlaceholderVisibility(List<Card> data) {
        if (data.size() != 0) {
            this.placeholder.setVisibility(View.GONE);
        } else {
            this.placeholder.setVisibility(View.VISIBLE);
        }
    }
}
