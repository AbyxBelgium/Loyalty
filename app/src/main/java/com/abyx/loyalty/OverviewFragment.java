package com.abyx.loyalty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment provides an overview of all loyalty cards stored on this device. These cards are
 * shown in a grid.
 *
 * @author Pieter Verschaffelt
 */
public class OverviewFragment extends Fragment {
    private static String DATA_ARG = "CARD_DATA";

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
            mainGrid.setMultiChoiceModeListener(new MultiChoiceGridViewListener(data, getActivity()));

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

    public interface OverviewFragmentInteractionListener {
        /**
         * This function will be called when the user selects a specific card.
         *
         * @param card The card the user clicked on.
         */
        public void onItemClicked(Card card);
    }
}
