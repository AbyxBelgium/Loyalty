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
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.extra.Constants;
import com.abyx.loyalty.extra.CardAdapter;
import com.abyx.loyalty.R;
import com.abyx.loyalty.extra.RecyclerItemListener;
import com.abyx.loyalty.extra.RecyclerTouchListener;
import com.abyx.loyalty.fragments.Filter;
import com.abyx.loyalty.fragments.ListInteractor;
import com.abyx.loyalty.managers.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends MainActivity implements TextWatcher {
    private EditText searchField;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // add the custom view to the action bar
            actionBar.setCustomView(R.layout.search_actionbar);

            searchField = (EditText) actionBar.getCustomView().findViewById(R.id.searchField);
            searchField.addTextChangedListener(this);

            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //Respond to search queries
        final String query = charSequence.toString().toLowerCase();
        overviewFragment.filter(new Filter<Card>() {
            @Override
            public boolean retain(Card item) {
                return item.getName().toLowerCase().contains(query);
            }
        });
    }
}
