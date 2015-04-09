package com.gans.vk.service;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface RatingService {

    void rate(User user, Song song, int value);

}
