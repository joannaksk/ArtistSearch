package com.ksaakstudio.joanna.artistsearch.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ksaakstudio.joanna.artistsearch.BuildConfig;
import com.ksaakstudio.joanna.artistsearch.models.Artist;
import com.ksaakstudio.joanna.artistsearch.models.Image;
import com.ksaakstudio.joanna.artistsearch.models.Results;
import com.ksaakstudio.joanna.artistsearch.models.Similarartists;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by joanna on 03/08/16.
 */
public class ArtistSearchService {
    private static final String API_KEY = BuildConfig.LAST_FM_API_KEY;
    public LastFMWebService mWebService;

    public ArtistSearchService() {

        // Create an RxJava adapter to pass to the web service.
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.LAST_FM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
        mWebService = retrofit.create(LastFMWebService.class);
    }

    // My LastFMWebService is only interested in two urls.
    public interface LastFMWebService {
        // The first searches for artists with the queried name.
        @GET("?method=artist.search&format=json&api_key=" + API_KEY)
        Observable<ResultsDataEnvelope> searchArtists(@Query("artist") String name);
        // The second searches for artists similar to the artist with the queried mbid.
        @GET("?method=artist.getsimilar&format=json&api_key=" + API_KEY)
        Observable<SimilarArtistsDataEnvelope> getSimilarArtists(@Query("mbid") String mbid);
    }

    /**
     * Base class for results returned by the artist search web service.
     */
    public class ResultsDataEnvelope {
        public Results results;
    }

    /**
     * Base class for similar artists returned by the artist search web service.
     */
    private class SimilarArtistsDataEnvelope {
        @SerializedName("similarartists")
        @Expose
        private Similarartists similarartists;

        public Observable<? extends SimilarArtistsDataEnvelope> filterWebServiceErrors() {
            // As long as the similar artists object return has artists, go on.
            if (similarartists.getArtist() != null) {
                return Observable.just(this);
            }
            return null;
        }
    }

    public Observable<List<Artist>> getSimilarArtists(final String mbid) {
        return mWebService.getSimilarArtists(mbid)
                .flatMap(new Func1<SimilarArtistsDataEnvelope,
                        Observable<? extends SimilarArtistsDataEnvelope>>() {

                    // Error out if the request was not successful.
                    @Override
                    public Observable<? extends SimilarArtistsDataEnvelope> call(
                            final SimilarArtistsDataEnvelope listData) {
                        return listData.filterWebServiceErrors();
                    }

                }).map(new Func1<SimilarArtistsDataEnvelope, List<Artist>>() {

                    // Parse the result and build a list of Artist objects.
                    @Override
                    public List<Artist> call(final SimilarArtistsDataEnvelope listData) {
                        final ArrayList<Artist> search_results =
                                new ArrayList<>();

                        for (Artist data : listData.similarartists.getArtist()) {
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
                            final Artist artist = new Artist(
                                    data.getName(),
                                    data.getListeners(),
                                    data.getMbid(),
                                    data.getUrl(),
                                    data.getStreamable(),
                                    images,
                                    data.getAdditionalProperties(),
                                    data.getMatch());
                            search_results.add(artist);
                        }

                        return search_results;
                    }
                });
    }
}
