package com.ksaakstudio.joanna.artistsearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.ksaakstudio.joanna.artistsearch.R;
import com.ksaakstudio.joanna.artistsearch.models.Artist;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by joanna on 03/08/16.
 *
 * Simple Adapter for Recycler View that contains the artist search results. For Multi Choice
 * support I use Big Nerd Ranch's MultiSelector.
 */
public class SavedArtistAdapter extends RecyclerView.Adapter<SavedArtistAdapter.ArtistViewHolder> {
    private final Context mContext;
    private final AppCompatActivity mActivity;
    private List<Artist> artistList;

    // Initialising a MultiSelector.
    private final MultiSelector mMultiSelector = new MultiSelector();


    // Initialising an ActionMode.CallBack.
    // I'm only interested in letting users delete saved artists.
    private final ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            mActivity.getMenuInflater().inflate(R.menu.saved_artist_list_item_context, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_delete_artist:
                    // Need to finish the action mode before doing the following,
                    // not after. No idea why, but it crashes.
                    actionMode.finish();
                    deleteArtists();
                    mMultiSelector.clearSelections();
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    public SavedArtistAdapter(Context mContext, AppCompatActivity activity) {
        this.mContext = mContext;
        this.artistList = null;
        this.mActivity = activity;
    }

    public class ArtistViewHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener{
        private final String LOG_TAG = ArtistViewHolder.class.getSimpleName();
        final ImageView artist_image;
        final TextView artist_name;
        final LinearLayout item;
        private Artist mArtist;

        public ArtistViewHolder(View itemView) {
            super(itemView, mMultiSelector);
            artist_image = (ImageView) itemView.findViewById(R.id.artist_image_imageview);
            artist_name = (TextView) itemView.findViewById(R.id.artist_name_textview);
            item = (LinearLayout) itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);
        }

        public void bindArtist(Artist artist) {
            mArtist = artist;
        }

        @Override
        public void onClick(View v) {
            if (mArtist == null) {
                return;
            }
            // If the activity is not in multiselect mode, open the artist's Last FM page.
            if (!mMultiSelector.tapSelection(this)) {
                Uri uri = Uri.parse(mArtist.getUrl());
                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(intent);
                    } else {
                        Log.d(LOG_TAG, "Couldn't call " + uri.toString() + ", no receiving apps installed!");
                    }
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            // On Long Click, start the mDeleteMode created earlier.
            mActivity.startSupportActionMode(mDeleteMode);
            mMultiSelector.setSelected(this, true);
            return true;
        }
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.saved_artist_list_item, parent, false);

        return new ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        final Artist artist = artistList.get(position);
        holder.bindArtist(artist);
        Uri image_uri = null;

        if (artist.getImageMedium() != null) {
           image_uri  = Uri.parse(artist.getImageMedium());
        }

        Picasso.with(mContext).cancelRequest(holder.artist_image);
        if (image_uri != null) {
            Picasso
                    .with(mContext)
                    .load(image_uri)
                    .into(holder.artist_image);
        } else {
            holder.artist_image.setImageResource(R.drawable.placeholderx64);
        }
        
        holder.artist_name.setText(artist.getName());
        holder.artist_image.setContentDescription(artist.getName());
    }

    @Override
    public int getItemCount() {
        if(artistList != null) {
            return artistList.size();
        }
        return 0;
    }

    /**
     * Function that sets the artist List for this adapter
     * @param artists
     * list of Artist images.
     */
    public void setArtistList(List<Artist> artists) {
        this.artistList = artists;
        notifyDataSetChanged();
    }

    /**
     * Function that deletes artists.
     */
    private void deleteArtists(){
        for (int i = artistList.size()-1; i >= 0; i--) {
            if (mMultiSelector.isSelected(i, 0)) {
                Artist artist = artistList.get(i);
                if (Artist.delete(artist)) {
                    // Since the artist is in the artist list and the database, delete it from both.
                    artistList.remove(i);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, getItemCount());
                }
            }
        }

    }
}
