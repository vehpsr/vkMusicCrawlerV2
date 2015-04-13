package com.gans.vk.service;

import java.util.List;

import com.gans.vk.model.impl.User;

public interface AudioDiscoveryService {

    List<AudioData> getAllUnratedSongs(User target, User user, int maxSongsOnPage);

    public static class AudioData {
        private long id;
        private String artist;
        private String title;
        private String url;
        private String avgArtistRating;

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getArtist() {
            return artist;
        }
        public void setArtist(String artist) {
            this.artist = artist;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String getAvgArtistRating() {
            return avgArtistRating;
        }
        public void setAvgArtistRating(String avgArtistRating) {
            this.avgArtistRating = avgArtistRating;
        }
    }
}
