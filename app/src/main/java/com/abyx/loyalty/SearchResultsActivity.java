package com.abyx.loyalty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity implements TextWatcher{
    private EditText searchField;
    private GridView mainGrid;

    private ArrayList<Card> data;
    private ArrayList<Card> originalData;
    private GridAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        mainGrid = (GridView) findViewById(R.id.mainGrid);
        ActionBar actionBar = getSupportActionBar();
        // add the custom view to the action bar
        actionBar.setCustomView(R.layout.search_actionbar);
        searchField = (EditText) actionBar.getCustomView().findViewById(R.id.searchField);
        searchField.addTextChangedListener(this);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);
        originalData = getIntent().getParcelableArrayListExtra("LIST");
        data = new ArrayList<>();
        adapter = new GridAdapter(this, originalData);
        mainGrid.setAdapter(adapter);
        mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultsActivity.this, DetailsActivity.class);
                intent.putExtra("POS", position);
                intent.putParcelableArrayListExtra("LIST", originalData);
                startActivity(intent);
            }
        });
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
            adapter.refresh(data);
        } else {
            data.clear();
            for (Card test : originalData) {
                if (test.getName().toLowerCase().contains(query)) {
                    data.add(test);
                }
            }
        }
        adapter.refresh(data);
    }
}
