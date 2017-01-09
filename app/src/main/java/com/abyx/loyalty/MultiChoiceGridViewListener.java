package com.abyx.loyalty;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;

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

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Every selected item is added to the selected list so that we
        // are able to delete these objects later on
        if (!selected.contains(contents.get(position))) {
            selected.add(contents.get(position));
        } else {
            IO temp = new IO(context);
            temp.removeData(((Card) contents.get(position)).getName());
            temp.removeData(((Card) contents.get(position)).getBarcode());
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
                for (Card current: selected){
                    temp.removeData(current.getName());
                    temp.removeData(current.getBarcode());
                    contents.remove(current);
                }
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
