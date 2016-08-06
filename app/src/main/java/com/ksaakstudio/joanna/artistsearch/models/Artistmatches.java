package com.ksaakstudio.joanna.artistsearch.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Artistmatches {

    private List<Artist> artist = new ArrayList<Artist>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public Artistmatches withArtist(List<Artist> artist) {
        this.artist = artist;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Artistmatches withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
