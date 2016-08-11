package com.ksaakstudio.joanna.artistsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ksaakstudio.joanna.artistsearch.adapters.SavedArtistAdapter;
import com.ksaakstudio.joanna.artistsearch.models.Artist;

import java.util.ArrayList;
import java.util.List;

import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class SavedArtistsFragment extends Fragment {

    private static final String LOG_TAG = SavedArtistsFragment.class.getSimpleName();
    private SavedArtistAdapter savedArtistAdapter;


    public SavedArtistsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.saved_artist_list_item_context, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_saved_artists, container, false);

        // Set up the results view.
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView resultsView = (RecyclerView) rootView.findViewById(R.id.saved_artists);
        resultsView.setHasFixedSize(true);
        resultsView.setLayoutManager(layout);

        // Initialise the adapter with the artists List.
        savedArtistAdapter = new SavedArtistAdapter(
                getActivity(),
                (AppCompatActivity) getActivity());

        // Set the results view adapter.
        resultsView.setAdapter(savedArtistAdapter);

        // Load the artists from the database.
        loadArtists();

        return rootView;
    }

    /**
     * Function that creates an Rx.single that loads a single list of all the artists currently
     * saved.
     * @return Single<List<Artist>>
     */
    @SuppressWarnings("JavaDoc")
    private Single<List<Artist>> savedArtists() {
        return Single.fromCallable(new Func0<List<Artist>>() {
            @Override
            public List<Artist> call() {
                List<Artist> artists = new ArrayList<>();
                long count = Artist.count(Artist.class);
                if (count > 0) {
                    artists = Artist.listAll(Artist.class);
                }
                return artists;
            }
        });
    }

    /**
     * Function that adds a subscription to the saved Artists single.
     */
    public void loadArtists() {
        savedArtists()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<Artist>>() {
                    @Override
                    public void onSuccess(List<Artist> value) {
                        savedArtistAdapter.setArtistList(value);
                    }

                    @Override
                    public void onError(Throwable error) {
                        // Notify the user on error.
                        Toast.makeText(getActivity(), "Load Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
