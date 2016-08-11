package com.ksaakstudio.joanna.artistsearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ksaakstudio.joanna.artistsearch.adapters.SimilarArtistAdapter;
import com.ksaakstudio.joanna.artistsearch.models.Artist;
import com.ksaakstudio.joanna.artistsearch.models.Image;
import com.ksaakstudio.joanna.artistsearch.services.ArtistSearchService;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by joanna on 03/08/16.
 * This activity shows another way to subscribe to a retrofit web service.
 *
 * In this class, we add a composite subscription to the contained fragment.
 */
public class SimilarArtistsActivity extends AppCompatActivity {
    public static final String ARTISTS_ID = "mbid";
    private static Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // We need to request this window feature so the progress bar will show.
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_artists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        artist = getIntent().getParcelableExtra(ARTISTS_ID);

        ImageView artistImage = (ImageView) findViewById(R.id.artist_image_imageview);

        // Try to get a large image from the artist and display it in the imageview in the
        // collapsing toolbar.
        if(!artist.getImage().isEmpty()) {
            List<Image> images = artist.getImage();
            Image largeImage = images.get(3);
            if (largeImage != null && !largeImage.getText().equals("")) {
                Uri largeImageUri = Uri.parse(largeImage.getText());
                Picasso.with(this).cancelRequest(artistImage);
                if (largeImageUri != null) {
                    Picasso
                            .with(this)
                            .load(largeImageUri)
                            .into(artistImage);
                } else {
                    artistImage.setImageResource(R.drawable.placeholderx64);
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add the similar artists fragment.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.similar_artists_fragment_container, new SimilarArtistsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_similar_artists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }


    public static class SimilarArtistsFragment extends Fragment {

        private static final long TIMEOUT_SECONDS = 30;
        private static final String LOG_TAG = SimilarArtistsFragment.class.getSimpleName();
        private CompositeSubscription mCompositeSubscription;
        private ArtistSearchService searchService;
        private List<Artist> mArtists;
        RecyclerView resultsView;
        private SimilarArtistAdapter similarArtistAdapter;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView =  inflater.inflate(R.layout.fragment_similar_artists, container, false);

            // Instantiate the a composite subscription.
            mCompositeSubscription = new CompositeSubscription();

            // Set up the results view.
            LinearLayoutManager layout = new LinearLayoutManager(getActivity());
            layout.setOrientation(LinearLayoutManager.VERTICAL);
            resultsView = (RecyclerView) rootView.findViewById(R.id.list_similar_artists);
            resultsView.setHasFixedSize(true);
            resultsView.setLayoutManager(layout);
            resultsView.setVisibility(View.GONE);

            // Initialise the adapter with the artists List.
            similarArtistAdapter = new SimilarArtistAdapter(getActivity(), mArtists, new SimilarArtistAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Uri uri) {
                    if (uri != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Log.d(LOG_TAG, "Couldn't call " + uri.toString() + ", no receiving apps installed!");
                        }
                    }
                }
            }, new SimilarArtistAdapter.OnItemLongClickListener() {
                // On Long Click persist this artist to the database.
                @Override
                public void onItemLongClick(Artist artist) {
                    artist.save();
                    Toast.makeText(getActivity(), "Artist saved.", Toast.LENGTH_SHORT).show();
                }
            });
            resultsView.setAdapter(similarArtistAdapter);

            // Call the method that actually subscribes to observables.
            loadSimilarArtists();
            return rootView;
        }

        @Override
        public void onDestroy(){
            // Remember to unsubscribe whenever the fragment is destroyed.
            mCompositeSubscription.unsubscribe();
            super.onDestroy();
        }

        /**
         * This function adds subscriptions to the composite subscription.
         */
        private void loadSimilarArtists() {
            String mbid = artist.getMbid();
            // Instantiate an ArtistSearchService.
            searchService = new ArtistSearchService();
            // Instantiate an Observable by calling the appropriate method.
            Observable<List<Artist>> fetchDataObservable = searchService.getSimilarArtists(mbid);

            // Add the observable.
            mCompositeSubscription.add(fetchDataObservable
                    .timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Artist>>() {

                        @Override
                        public void onNext(List<Artist> artists) {
                            // When getSimilarArtists() completes, it returns a list of artist
                            // objects.
                            // Pass those artists to the adapter.
                            mArtists = artists;
                            similarArtistAdapter.setArtistList(artists);
                        }
                        @Override
                        public void onCompleted() {
                            // When all is done, make the results view visible and hide the progress
                            // bar and fab.
                            resultsView.setVisibility(View.VISIBLE);
                            RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.fab_loading);
                            layout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // Notify the user on error.
                            Toast.makeText(getActivity(), "Download Error", Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }
    }

}
