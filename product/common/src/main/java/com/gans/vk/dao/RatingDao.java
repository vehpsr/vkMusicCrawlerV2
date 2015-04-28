package com.gans.vk.dao;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface RatingDao extends ModelDao<Rating> {

    Rating getBy(User user, Song song);
    void importUserAudioLib(User user, Set<Entry<String, String>> audioLib);
    Map<Date, Entry<Integer, Float>> rating(User user, long from, long to, long step);
}
