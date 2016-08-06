package com.ksaakstudio.joanna.artistsearch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Similarartists {

    @SerializedName("artist")
    @Expose
    private List<Artist> artist = new ArrayList<Artist>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Similarartists() {
    }

    /**
     *
     * @param attr
     * @param artist
     */
    public Similarartists(List<Artist> artist) {
        this.artist = artist;
    }

    /**
     * 
     * @return
     *     The artist
     */
    public List<Artist> getArtist() {
        return artist;
    }

    /**
     * 
     * @param artist
     *     The artist
     */
    public void setArtist(List<Artist> artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
