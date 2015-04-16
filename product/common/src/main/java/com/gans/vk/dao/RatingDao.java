package com.gans.vk.dao;

import java.util.List;
import java.util.Map.Entry;

import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface RatingDao extends ModelDao<Rating> {

    Rating getBy(User user, Song song);
    void importUserAudioLib(User user, List<Entry<String, String>> audioLib);

}
