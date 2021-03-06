package com.gans.vk.model.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.gans.vk.model.AbstractModel;

@Entity
@Table(name="Users")
public class User extends AbstractModel {

    public enum UserStatus {
        PARSER_ERROR, DELETED, CLOSED_PAGE, NOT_ENOUGH_AUDIO, TO_MANY_AUDIO, RESOLVED;

        public static Collection<String> names() {
            List<String> names = new ArrayList<>();
            for (UserStatus status : values()) {
                names.add(status.name());
            }
            return names;
        }
    }

    public static final int NAME_MAX_LEN = 60;
    public static final int URL_MAX_LEN = 40;
    public static final int VK_ID_MAX_LEN = 20;

    @Column(length = NAME_MAX_LEN)
    private String _name;

    @Column(length = URL_MAX_LEN)
    private String _url;

    @Column(length = VK_ID_MAX_LEN)
    private String _vkId;

    private Set<Rating> _ratings;

    public User() {
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

    @Override
    public String toString() {
        return MessageFormat.format("{0}: {1} {2} {3}", getId(), _name, _url, _vkId);
    }
}
