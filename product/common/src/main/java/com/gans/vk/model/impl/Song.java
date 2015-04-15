package com.gans.vk.model.impl;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.gans.vk.model.AbstractModel;

@Entity
public class Song extends AbstractModel {

    public static final int ARTIST_MAX_LEN = 50;
    public static final int TITLE_MAX_LEN = 70;

    @Column(length = ARTIST_MAX_LEN)
    private String _artist;

    @Column(length = TITLE_MAX_LEN)
    private String _title;

    private Set<Rating> _ratings;

    public Song() {
        _ratings = new HashSet<>();
    }

    public Song(String artist, String title) {
        this();
        _artist = artist;
        _title = title;
    }

    @OneToMany(mappedBy="song")
    public Set<Rating> getRatings() {
        return _ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        _ratings = ratings;
    }

    public void addRating(Rating rating) {
        _ratings.add(rating);
    }

    public String getArtist() {
        return _artist;
    }

    public void setArtist(String artist) {
        _artist = artist;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} {1} - {2}", getId(), _artist, _title);
    }

}
