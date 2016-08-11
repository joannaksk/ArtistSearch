package com.ksaakstudio.joanna.artistsearch;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ksaakstudio.joanna.artistsearch.adapters.ArtistAdapter;
import com.ksaakstudio.joanna.artistsearch.models.Artist;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by joanna on 03/08/16.
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultsFragment extends Fragment {

    private static final String KEY_ARTISTS = "artists";
    private OnFragmentInteractionListener mListener;
    private List<Artist> mArtists;
    private ArtistAdapter artistAdapter;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param artists List of search results(artists).
     *
     * @return A new instance of fragment SearchResultsFragment.
     */
    public static SearchResultsFragment newInstance(List<Artist> artists) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        // Because my Artist object is parcelable, I can use putParcelableArrayList().
        args.putParcelableArrayList(KEY_ARTISTS, (ArrayList<? extends Parcelable>) artists);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Instantiate mArtists with the list of Artists in the arguments.
            mArtists = getArguments().getParcelableArrayList(KEY_ARTISTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_search_results, container, false);

        // Set up the results view.
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView resultsView = (RecyclerView) rootView.findViewById(R.id.list_search_results);
        resultsView.setHasFixedSize(true);
        resultsView.setLayoutManager(layout);

        // Initialise the adapter with the artists List.
        artistAdapter = new ArtistAdapter(getActivity(), mArtists, new ArtistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Artist artist) {
                if (artist != null) {
                    mListener = ((OnFragmentInteractionListener) getActivity());
                    mListener.onItemSelected(artist);
                }
            }
        });

        // set the results view adapter.
        resultsView.setAdapter(artistAdapter);

        final MainActivity activity = (MainActivity) getActivity();
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clicking this button lets the user search for another artist.
                activity.searchAgain();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onItemSelected(Artist artist);
    }
}
