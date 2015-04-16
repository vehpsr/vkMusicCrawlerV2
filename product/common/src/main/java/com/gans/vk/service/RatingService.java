package com.gans.vk.service;

import java.util.List;
import java.util.Map.Entry;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface RatingService {

    void rate(User user, Song song, int value);
    void importUserAudioLib(User user, List<Entry<String, String>> audioLib);

}
