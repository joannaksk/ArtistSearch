package com.ksaakstudio.joanna.artistsearch;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ksaakstudio.joanna.artistsearch.adapters.DrawerAdapter;
import com.ksaakstudio.joanna.artistsearch.models.Artist;
import com.ksaakstudio.joanna.artistsearch.models.Image;
import com.ksaakstudio.joanna.artistsearch.services.ArtistSearchService;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by joanna on 03/08/16.
 * This activity shows a simple way to subscribe to a retrofit web service.
 *
 * In this class, we add a single subscription to the contained search fragment.
 */
public class  MainActivity extends AppCompatActivity implements SearchResultsFragment.OnFragmentInteractionListener{

    private static final String SEARCH_FRAGMENT_TAG = "SFTAG";
    private static final String RESULTS_FRAGMENT_TAG = "RFTAG";
    private SearchFragment searchFragment;
    private MenuItem searchActionProgressItem;
    private ProgressBar progressBar;

    // Drawer related variables.
    private String[] mActivitiesList;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // We need to request this window feature so the progress bar will show.
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the rawer List View.
        mActivitiesList = getResources().getStringArray(R.array.activity_names);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the drawer list view
        mDrawerList.setAdapter(new DrawerAdapter(this,
                mActivitiesList));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        //noinspection deprecation
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Instantiate a search fragment.
        searchFragment = new SearchFragment();

        // Add it to this activity.
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_fragment_container, searchFragment, SEARCH_FRAGMENT_TAG).commit();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        searchActionProgressItem = menu.findItem(R.id.searchActionProgress);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Function to show the progress bar.
     */
    private void showProgressBar() {
        searchActionProgressItem.setVisible(true);
    }

    /**
     * Function to hide the progress bar.
     */
    private void hideProgressBar() {
        searchActionProgressItem.setVisible(false);
    }

    /**
     * Function that adds a subscription to the ArtistSearchService.
     * @param artist
     * list of Artist images.
     */
    public void searchArtists(String artist) {
        showProgressBar();

        // Instantiate an artist search service.
        final ArtistSearchService searchService = new ArtistSearchService();

        // Subscribe to the observable returned by the searchArtists() function.
        searchService
                .mWebService.searchArtists(artist)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArtistSearchService.ResultsDataEnvelope>() {

                    // Declare a results fragment.
                    SearchResultsFragment resultsFragment;
                    @Override
                    public final void onCompleted() {
                        // When all is done, hide the progress bar and switch out the search
                        // fragment for the results fragment.
                        hideProgressBar();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.search_fragment_container, resultsFragment, RESULTS_FRAGMENT_TAG)
                                .commit();
                    }

                    @Override
                    public final void onError(Throwable e) {
                        // Notify the user on error.
                        Toast.makeText(getApplicationContext(), "Download Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ArtistSearchService.ResultsDataEnvelope resultsDataEnvelope) {

                        ArrayList<Artist> artists =
                                new ArrayList<>();

                        // Look into the resultsDataEnvelope returned and find the list of artists.
                        // Iterate over those and add each to the artists list.
                        for (Artist data : resultsDataEnvelope.results.getArtistmatches().getArtist()) {
                            // Each artist should get it's own new list of Image objects.
                            ArrayList<Image> images =
                                    new ArrayList<>();
                            for(Image image_data : data.getImage()) {
                                Image image = new Image(
                                        image_data.getText(),
                                        image_data.getSize(),
                                        image_data.getAdditionalProperties());
                                images.add(image);
                            }

                            Artist artist = new Artist(
                                    data.getName(),
                                    data.getListeners(),
                                    data.getMbid(),
                                    data.getUrl(),
                                    data.getStreamable(),
                                    images,
                                    data.getAdditionalProperties());
                            artists.add(artist);
                        }
                        // Instantiate the results fragment with the artists list.
                        resultsFragment = SearchResultsFragment.newInstance(artists);
                    }
                });
    }

    /**
     * Function that switches out results fragment for a search fragment so the user can search
     * again.
     */
    public void searchAgain() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_fragment_container,  searchFragment, SEARCH_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onItemSelected(Artist artist) {
        // Selecting an item in the search results launches the similar artists activity.
        Intent intent = new Intent(this, SimilarArtistsActivity.class);
        intent.putExtra(SimilarArtistsActivity.ARTISTS_ID, artist);
        startActivity(intent);

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps activities in the main content view */
    private void selectItem(int position) {
        Intent intent;
        switch(position) {
            case 0:
                intent = new Intent(this, MainActivity.class);
                break;
            case 1:
                intent = new Intent(this, SavedArtistsActivity.class);
                break;
            default :
                intent = new Intent(this, MainActivity.class);
                break;
        }
        startActivity(intent);
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mActivitiesList[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
}
