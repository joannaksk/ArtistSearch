package com.ksaakstudio.joanna.artistsearch.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksaakstudio.joanna.artistsearch.R;
import com.ksaakstudio.joanna.artistsearch.models.Artist;
import com.ksaakstudio.joanna.artistsearch.models.Image;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by joanna on 03/08/16.
 * Simple Adapter for Recycler View that contains the artist search results.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private final Context mContext;
    private List<Artist> artistList;
    private final OnItemClickListener listener;

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        protected ImageView artist_image;
        protected TextView artist_name;
        protected TextView artist_listeners;
        protected LinearLayout item;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            artist_image = (ImageView) itemView.findViewById(R.id.artist_image_imageview);
            artist_name = (TextView) itemView.findViewById(R.id.artist_name_textview);
            artist_listeners = (TextView) itemView.findViewById(R.id.artist_listeners_textview);
            item = (LinearLayout) itemView;
        }
    }

    public ArtistAdapter(Context mContext, List<Artist> artists, OnItemClickListener listener) {
        this.mContext = mContext;
        this.artistList = artists;
        this.listener = listener;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.artist_list_item, parent, false);

        return new ArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        final Artist artist = artistList.get(position);
        Uri image_uri = null;

        List<Image> images = artist.getImage();
        if (images.size() != 0 && images.get(1).getText() != "") {
           image_uri  = Uri.parse(images.get(1).getText());
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

        holder.artist_listeners.setText(String.format(mContext.getString(R.string.listeners_format),
                artist.getListeners()));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(artist);
            }
        });

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
     */
    public void setArtistList(List<Artist> artists) {
        this.artistList = artists;
        notifyDataSetChanged();
    }

    /**
     * This interface is how I handle recycler view click events.
     */
    public interface OnItemClickListener {
        void onItemClick(Artist artist);
    }
}
