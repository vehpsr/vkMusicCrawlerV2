package com.gans.vk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface RatingService {

    void rate(User user, Song song, int value);
    void importUserAudioLib(User user, Set<Entry<String, String>> audioLib);
    Entry<Map<Long, Float>, List<RatingData>> rating(User user);

    public static class RatingData {
        private String _key;
        private List<Point> _values = new ArrayList<>();

        public String getKey() {
            return _key;
        }

        public void setKey(String key) {
            _key = key;
        }

        public List<Point> getValues() {
            return _values;
        }

        public void addPoint(long x, int y) {
            _values.add(new Point(x, y));
        }

    }

    public static class Point {
        private final long _x;
        private final int _y;

        public Point(long x, int y) {
            _x = x;
            _y = y;
        }

        public long getX() {
            return _x;
        }

        public int getY() {
            return _y;
        }

    }
}
