package com.gans.vk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface RatingService {

    void rate(User user, Song song, int value);
    void importUserAudioLib(User user, Set<Entry<String, String>> audioLib);
    List<UserRatingData> rating(final User user, final User target);

    public static class UserRatingData {
        private String _key;
        private boolean _bar;
        private List<Number[]> _values = new ArrayList<>();

        public boolean isBar() {
            return _bar;
        }

        public void setBar(boolean bar) {
            _bar = bar;
        }

        public String getKey() {
            return _key;
        }

        public void setKey(String key) {
            _key = key;
        }

        public List<Number[]> getValues() {
            return _values;
        }

        public void addValue(Number[] value) {
            _values.add(value);
        }

    }
}
