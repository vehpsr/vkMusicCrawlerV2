package com.gans.vk.model.impl;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.gans.vk.model.AbstractModel;

@Entity
public class Rating extends AbstractModel {

    private int _value;
    @ManyToOne
    private User _user;
    @ManyToOne
    private Song _song;

    public Rating(int value, User user, Song song) {
        _value = value;
        _user = user;
        _song = song;
    }

    public int getValue() {
        return _value;
    }

    public void setValue(int value) {
        _value = value;
    }

    public User getUser() {
        return _user;
    }

    public void setUser(User user) {
        _user = user;
    }

    public Song getSong() {
        return _song;
    }

    public void setSong(Song song) {
        _song = song;
    }
}
