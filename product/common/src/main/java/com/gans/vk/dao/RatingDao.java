package com.gans.vk.dao;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.google.common.collect.Multimap;

public interface RatingDao extends ModelDao<Rating> {

    Rating getBy(User user, Song song);
    void importUserAudioLib(User user, Set<Entry<String, String>> audioLib);
    Multimap<Date, Entry<Integer, Integer>> rating(User user, long from, long to, long step);
    int ratedByUserCount(User user);
}
