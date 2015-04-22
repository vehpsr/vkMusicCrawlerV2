package com.gans.vk.dao;

import java.util.List;
import java.util.Map.Entry;

import com.gans.vk.model.impl.User;

public interface UserDao extends ModelDao<User> {

    User getUserByUrl(String name);
    User getUserByVkId(String vkId);
    List<UserLibData> getRecomendedAudioLibsFor(User user);
    void importUnique(List<Entry<String, String>> users);
    int getUndiscoveredUsersCount();

    public static class UserLibData {
        private long _id;
        private String _name;
        private String _url;
        private String _vkId;
        private float _rating;
        private int _ratedAudioCount;
        private int _totalAudioCount;

        public String getName() {
            return _name;
        }
        public void setName(String name) {
            _name = name;
        }
        public long getId() {
            return _id;
        }
        public void setId(long id) {
            _id = id;
        }
        public String getUrl() {
            return _url;
        }
        public void setUrl(String url) {
            _url = url;
        }
        public String getVkId() {
            return _vkId;
        }
        public void setVkId(String vkId) {
            _vkId = vkId;
        }
        public float getRating() {
            return _rating;
        }
        public void setRating(float rating) {
            _rating = rating;
        }
        public int getRatedAudioCount() {
            return _ratedAudioCount;
        }
        public void setRatedAudioCount(int ratedAudioCount) {
            _ratedAudioCount = ratedAudioCount;
        }
        public int getTotalAudioCount() {
            return _totalAudioCount;
        }
        public void setTotalAudioCount(int totalAudioCount) {
            _totalAudioCount = totalAudioCount;
        }
    }

}
