package com.gans.vk.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.RatingDao;
import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.RatingService;

public class RatingServiceImpl implements RatingService {

    private static final long SECONDS_PER_DAY = DateUtils.MILLIS_PER_DAY / 1000;

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
    public void importUserAudioLib(User user, Set<Entry<String, String>> audioLib) {
        _ratingDao.importUserAudioLib(user, audioLib);
    }

    @Override
    public List<UserRatingData> rating(User user, User target) {
        Calendar calendar = GregorianCalendar.getInstance();
        long to = calendar.getTimeInMillis() + SECONDS_PER_DAY;
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        long from = calendar.getTimeInMillis();
        Map<Date, Entry<Integer, Float>> ratingStats = _ratingDao.rating(user, target, from, to, SECONDS_PER_DAY);

        UserRatingData countData = new UserRatingData();
        countData.setKey("count");

        UserRatingData avgRatingData = new UserRatingData();
        avgRatingData.setKey("avgRating");

        for (Entry<Date, Entry<Integer, Float>> dataPoint : ratingStats.entrySet()) {
            Entry<Integer, Float> stats = dataPoint.getValue();
            countData.addPoint(dataPoint.getKey().getTime(), stats.getKey());
            avgRatingData.addPoint(dataPoint.getKey().getTime(), stats.getValue());
        }

        List<UserRatingData> result = new ArrayList<>();
        result.add(avgRatingData);
        result.add(countData);
        return result;
    }

    public void setRatingDao(RatingDao ratingDao) {
        _ratingDao = ratingDao;
    }

}
