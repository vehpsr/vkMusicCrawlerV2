package com.gans.vk.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
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
import com.google.common.collect.Multimap;

public class RatingServiceImpl implements RatingService {

    private static final long SECONDS_PER_DAY = DateUtils.MILLIS_PER_DAY / 1000;

    @Autowired private RatingDao _ratingDao;

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
    public List<RatingData> rating(User user) {
        Calendar calendar = GregorianCalendar.getInstance();
        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -14);
        long from = calendar.getTimeInMillis();
        Multimap<Date, Entry<Integer, Integer>> ratingStats = _ratingDao.rating(user, from, to, SECONDS_PER_DAY);
        if (ratingStats.isEmpty()) {
            return Collections.emptyList();
        }

        System.out.println(ratingStats);
        System.out.println();

        Map<Integer, Map<Date, Integer>> datum = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            datum.put(i, new LinkedHashMap<Date, Integer>());
            for (Date date : ratingStats.keySet()) {
                datum.get(i).put(date, 0);
            }
        }

        for (Date date : ratingStats.keySet()) {
            Collection<Entry<Integer, Integer>> ratingData = ratingStats.get(date);
            for (Entry<Integer, Integer> entry : ratingData) {
                int ratingValue = entry.getKey();
                int count = entry.getValue();
                datum.get(ratingValue).put(date, count);
            }
        }

        System.out.println(datum);

        List<RatingData> result = new ArrayList<>();

        RatingData countData = new RatingData();
        countData.setKey("count");

        RatingData avgRatingData = new RatingData();
        avgRatingData.setKey("avgRating");
/*
        for (Entry<Date, Entry<Integer, Integer>> dataPoint : ratingStats.entrySet()) {
            Entry<Integer, Float> stats = dataPoint.getValue();
            countData.addPoint(dataPoint.getKey().getTime(), stats.getKey());
            avgRatingData.addPoint(dataPoint.getKey().getTime(), stats.getValue());
        }*/

        result.add(avgRatingData);
        result.add(countData);
        return result;
    }

    public void setRatingDao(RatingDao ratingDao) {
        _ratingDao = ratingDao;
    }

}
