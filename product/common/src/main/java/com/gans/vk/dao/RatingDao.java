package com.gans.vk.dao;

import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface RatingDao extends ModelDao<Rating> {

    Rating getBy(User user, Song song);

}
