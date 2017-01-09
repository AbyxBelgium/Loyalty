package com.abyx.loyalty.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.abyx.loyalty.contents.Card;
import com.abyx.loyalty.extra.GridAdapter;
import com.abyx.loyalty.contents.IO;
import com.abyx.loyalty.extra.MultiChoiceGridViewListener;
import com.abyx.loyalty.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment provides an overview of all loyalty cards stored on this device. These cards are
 * shown in a grid.
 *
 * @author Pieter Verschaffelt
 */
public class OverviewFragment extends Fragment {
    private static final String DATA_ARG = "CARD_DATA";

    private GridView mainGrid;

    private List<Card> data;
    private GridAdapter adapter;
    private OverviewFragmentInteractionListener listener;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment.
     *
     * @param data A list containing all cards that are held by this application.
     * @return A new instance of fragment OverviewFragment.
     */
    public static OverviewFragment newInstance(ArrayList<Card> data) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATA_ARG, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        Bundle args = getArguments();
        if (args != null) {
            data = args.getParcelableArrayList(DATA_ARG);
            mainGrid = (GridView) view.findViewById(R.id.mainGrid);
            adapter = new GridAdapter(getActivity(), data);
            mainGrid.setAdapter(adapter);

            mainGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
            mainGrid.setMultiChoiceModeListener(new MultiChoiceGridViewListener(data, getActivity(), new MultiChoiceGridViewListener.DeleteListener() {
                @Override
                public void itemDeleted(List<Card> list) {
                    data = list;
                    adapter.refresh(data);
                }
            }));

            mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (listener != null) {
                        // Inform the surrounding activity that a card was selected by the user!
                        listener.onItemClicked(data.get(position));
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OverviewFragmentInteractionListener) {
            listener = (OverviewFragmentInteractionListener) context;
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
    public void onPause() {
        super.onPause();
        if (isPermissionGranted(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // Save all data to internal storage
            IO temp = new IO(getActivity());
            temp.save(data);
        }
    }

    /**
     * TODO this is code duplication of PermissionActivity
     *
     * @param activity The activity calling this method
     * @param permission The permission for which you want to know if it's granted
     * @return Returns true when the given permission has already been granted by the user
     */
    protected boolean isPermissionGranted(Activity activity, String permission){
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Call this method when anything (even the order of items) changes to the data-array. This
     * function will refresh the adapter, meaning it will update it's UI according to the new
     * list.
     *
     * @param data Updated list containing all cards
     */
    public void refreshData(ArrayList<Card> data) {
        this.data = data;
        adapter.refresh(data);
    }

    public interface OverviewFragmentInteractionListener {
        /**
         * This function will be called when the user selects a specific card.
         *
         * @param card The card the user clicked on.
         */
        public void onItemClicked(Card card);
    }
}
