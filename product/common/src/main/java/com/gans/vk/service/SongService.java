package com.gans.vk.service;

import java.text.MessageFormat;
import java.util.List;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface SongService {

    List<SongData> getAllUnratedSongs(User target, User user, int limit);
    Song get(long id);

    public static class SongData {
        private long _id;
        private String _artist;
        private String _title;
        private int _artistRateCount;
        private float _artistAvgRating;

        public long getId() {
            return _id;
        }
        public void setId(long id) {
            _id = id;
        }
        public String getArtist() {
            return _artist;
        }
        public void setArtist(String artist) {
            _artist = artist;
        }
        public String getTitle() {
            return _title;
        }
        public void setTitle(String title) {
            _title = title;
        }
        public int getArtistRateCount() {
            return _artistRateCount;
        }
        public void setArtistRateCount(int artistRateCount) {
            _artistRateCount = artistRateCount;
        }
        public float getArtistAvgRating() {
            return _artistAvgRating;
        }
        public void setArtistAvgRating(float artistAvgRating) {
            _artistAvgRating = artistAvgRating;
        }
        @Override
        public String toString() {
            return MessageFormat.format("{0}: {1} - {2}", _id, _artist, _title);
        }
    }
}
