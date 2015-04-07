package com.gans.vk.model.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.gans.vk.model.AbstractModel;

@Entity
@Table(name="Users")
public class User extends AbstractModel {

    private String _name;
    private String _url;
    private String _vkId;
    private Set<Rating> _ratings;
    private Set<Song> _songs;

    public User() {
        _songs = new HashSet<>();
        _ratings = new HashSet<>();
    }

    @OneToMany(mappedBy="user")
    public Set<Rating> getRatings() {
        return _ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        _ratings = ratings;
    }

    public void addRating(Rating rating) {
        _ratings.add(rating);
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String url) {
        _url = url;
    }

    public String getVkId() {
        return _vkId;
    }

    public void setVkId(String vkId) {
        _vkId = vkId;
    }

    @ManyToMany
    public Set<Song> getSongs() {
        return _songs;
    }

    public void setSongs(Set<Song> songs) {
        _songs = songs;
    }

    public void addSong(Song song) {
        _songs.add(song);
    }

}
