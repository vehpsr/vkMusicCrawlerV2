package com.gans.vk.model.impl;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.gans.vk.model.AbstractModel;

@Entity
public class Rating extends AbstractModel {

    private int _value;
    private Date _date;
    private User _user;
    private Song _song;

    public Rating() {
        _date = new Date();
    }

    public Rating(int value, User user, Song song) {
        this();
        _value = value;
        _user = user;
        _song = song;
    }

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
    }

    public int getValue() {
        return _value;
    }

    public void setValue(int value) {
        _value = value;
    }

    @ManyToOne
    public User getUser() {
        return _user;
    }

    public void setUser(User user) {
        _user = user;
    }

    @ManyToOne
    public Song getSong() {
        return _song;
    }

    public void setSong(Song song) {
        _song = song;
    }
}
