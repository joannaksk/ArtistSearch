package com.ksaakstudio.joanna.artistsearch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by joanna on 03/08/16.
 * A placeholder fragment containing two simple views and a fab.
 */
public class SearchFragment extends Fragment {

    public SearchFragment () {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final EditText editText = (EditText) rootView.findViewById(R.id.artist_search);
        final MainActivity activity = (MainActivity) getActivity();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clicking thi button launches the search for an artist.
                String artist = editText.getText().toString();
                activity.searchArtists(artist);
            }
        });

        return rootView;
    }
}
