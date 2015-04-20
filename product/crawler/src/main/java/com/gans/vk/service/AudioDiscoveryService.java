package com.gans.vk.service;

import java.util.List;

import com.gans.vk.model.impl.User;

public interface AudioDiscoveryService {

    List<AudioData> getAllUnratedSongs(User target, User user, int maxSongsOnPage);
    void discoverAudioByUserUrl(String url, boolean forceUpdate);

    public static class AudioData {
        private long _id;
        private String _artist;
        private String _title;
        private String _url;
        private String _time;
        private float _artistAvgRating;
        private int _artistRateCount;

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
        public String getUrl() {
            return _url;
        }
        public void setUrl(String url) {
            this._url = url;
        }
        public String getTime() {
            return _time;
        }
        public void setTime(String time) {
            _time = time;
        }
        public float getArtistAvgRating() {
            return _artistAvgRating;
        }
        public void setArtistAvgRating(float artistAvgRating) {
            _artistAvgRating = artistAvgRating;
        }
        public int getArtistRateCount() {
            return _artistRateCount;
        }
        public void setArtistRateCount(int artistRateCount) {
            _artistRateCount = artistRateCount;
        }
    }
}
