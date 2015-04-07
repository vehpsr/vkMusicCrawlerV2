package com.gans.vk.model.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.gans.vk.model.AbstractModel;

@Entity
public class Song extends AbstractModel {

    private String _artist;
    private String _title;
    private String _url;
    private Set<Rating> _ratings;
    private Set<User> _users;

    public Song() {
        _ratings = new HashSet<>();
        _users = new HashSet<>();
    }

    public Song(String artist, String title, String url) {
        this();
        _artist = artist;
        _title = title;
        _url = url;
    }

    @ManyToMany(mappedBy="songs")
    public Set<User> getUsers() {
        return _users;
    }

    public void setUsers(Set<User> users) {
        _users = users;
    }

    public void addUser(User user) {
        _users.add(user);
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

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }
}
