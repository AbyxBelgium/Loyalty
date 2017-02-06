package com.abyx.loyalty.extra;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.abyx.loyalty.R;
import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.contents.Database;
import com.abyx.loyalty.contents.IO;
import com.abyx.loyalty.exceptions.InvalidCardException;

import java.util.ArrayList;
import java.util.List;

/**
 * This listener can be used for GridViews that support the selection of multiple objects at
 * the same time.
 *
 * @author Pieter Verschaffelt
 * @see android.widget.GridView
 */
public class MultiChoiceGridViewListener implements AbsListView.MultiChoiceModeListener {
    private List<Card> contents;
    private List<Card> selected = new ArrayList<>();
    private Context context;
    private DeleteListener listener;

    public MultiChoiceGridViewListener(List<Card> contents, Context context, DeleteListener listener){
        this.contents = contents;
        this.context = context;
        this.listener = listener;
    }

    public void updateContents(List<Card> contents) {
        this.contents = contents;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Every selected item is added to the selected list so that we
        // are able to delete these objects later on
        if (!selected.contains(contents.get(position))) {
            selected.add(contents.get(position));
        } else {
            selected.remove(contents.get(position));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
            case R.id.action_removeSelected:
                // All selected items have to be removed
                IO temp = new IO(context);
                Database db = new Database(context);
                db.openDatabase();
                try {
                    for (Card current : selected) {
                        temp.removeData(current.getName());
                        temp.removeData(current.getBarcode());
                        contents.remove(current);
                        db.deleteCard(current);
                    }
                } catch (InvalidCardException e) {
                    // TODO make a proper error message for this.
                    e.printStackTrace();
                }
                db.closeDatabase();
                listener.itemDeleted(contents);
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    public interface DeleteListener {
        /**
         * This function is called when data has been deleted from the list.
         *
         * @param data The new, altered list
         */
        void itemDeleted(List<Card> data);
    }
}
