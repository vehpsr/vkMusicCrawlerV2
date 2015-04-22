package com.gans.vk.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.RatingDao;
import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.RatingService;

public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingDao _ratingDao;

    @Override
    public void rate(User user, Song song, int value) {
        Rating rating = _ratingDao.getBy(user, song);
        if (rating != null) {
            rating.setDate(new Date());
            rating.setValue(value);
        } else {
            rating = new Rating(value, user, song);
        }
        _ratingDao.save(rating);
    }

    @Override
    public void importUserAudioLib(User user, List<Entry<String, String>> audioLib) {
        _ratingDao.importUserAudioLib(user, audioLib);
    }

    public void setRatingDao(RatingDao ratingDao) {
        _ratingDao = ratingDao;
    }

}
