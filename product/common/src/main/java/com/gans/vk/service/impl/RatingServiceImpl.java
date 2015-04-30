package com.gans.vk.service.impl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
    public Entry<Map<Long, Float>, List<RatingData>> rating(User user) {
        Calendar calendar = GregorianCalendar.getInstance();
        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -14);
        long from = calendar.getTimeInMillis();
        Multimap<Date, Entry<Integer, Integer>> ratingStats = _ratingDao.rating(user, from, to, SECONDS_PER_DAY);

        Map<Long, Float> avgRatingData = new LinkedHashMap<>();
        Map<Integer, Map<Date, Integer>> countData = new LinkedHashMap<>();

        for (int i = 1; i <= 5; i++) {
            countData.put(i, new LinkedHashMap<Date, Integer>());
            for (Date date : ratingStats.keySet()) {
                countData.get(i).put(date, 0);
            }
        }

        for (Date date : ratingStats.keySet()) {
            Collection<Entry<Integer, Integer>> ratingData = ratingStats.get(date);
            int totalCount = 0;
            int totalRating = 0;
            for (Entry<Integer, Integer> entry : ratingData) {
                int ratingValue = entry.getKey();
                int count = entry.getValue();
                countData.get(ratingValue).put(date, count);

                totalCount += count;
                totalRating += count * ratingValue;
            }
            avgRatingData.put(date.getTime(), (float) totalRating / totalCount);
        }

        List<RatingData> result = new ArrayList<>();
        for (Integer value : countData.keySet()) {
            RatingData ratingData = new RatingData();
            ratingData.setKey("" + value);
            Map<Date, Integer> dataPoints = countData.get(value);
            for (Entry<Date, Integer> dataPoint : dataPoints.entrySet()) {
                ratingData.addPoint(dataPoint.getKey().getTime(), dataPoint.getValue());
            }
            result.add(ratingData);
        }
        return new AbstractMap.SimpleEntry<Map<Long, Float>, List<RatingData>>(avgRatingData, result);
    }

    @Override
    public List<Entry<String, Integer>> statisticsRatingData(User user) {
        int ratedByUser = _ratingDao.ratedByUserCount(user);
        int total = _ratingDao.countAll();

        List<Entry<String, Integer>> result = new ArrayList<>();
        result.add(new AbstractMap.SimpleEntry<String, Integer>("Total rated", total));
        result.add(new AbstractMap.SimpleEntry<String, Integer>("Rated by me", ratedByUser));
        return result;
    }

    public void setRatingDao(RatingDao ratingDao) {
        _ratingDao = ratingDao;
    }

}
