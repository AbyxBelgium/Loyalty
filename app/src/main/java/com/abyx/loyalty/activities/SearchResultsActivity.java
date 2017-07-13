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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.CardAdapter;
import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.RecyclerItemListener;
import com.abyx.loyalty.extra.RecyclerTouchListener;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity implements TextWatcher {
    private EditText searchField;
    private RecyclerView mainList;

    private ArrayList<Card> data;
    private ArrayList<Card> originalData;
    private CardAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        mainList = (RecyclerView) findViewById(R.id.mainList);

        ActionBar actionBar = getSupportActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.search_actionbar);

        searchField = (EditText) actionBar.getCustomView().findViewById(R.id.searchField);
        searchField.addTextChangedListener(this);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);

        originalData = getIntent().getParcelableArrayListExtra("LIST");

        data = new ArrayList<>();
        data.addAll(originalData);

        adapter = new CardAdapter(data, getApplicationContext());
        mainList.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mainList.setLayoutManager(llm);

        mainList.addOnItemTouchListener(new RecyclerItemListener(getApplicationContext(), mainList, new RecyclerTouchListener() {
            @Override
            public void onClickItem(View v, int position) {
                Intent intent = new Intent(SearchResultsActivity.this, CardActivity.class);
                intent.putExtra(Constants.INTENT_CARD_ID_ARG, data.get(position).getID());
                startActivity(intent);
            }
        }));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Nothing to do here
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //Nothing to do here
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Respond to search queries
        String query = charSequence.toString().toLowerCase();
        if (query.equals("")){
            data.clear();
            data.addAll(originalData);
        } else {
            data.clear();
            for (Card test : originalData) {
                if (test.getName().toLowerCase().contains(query)) {
                    data.add(test);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
