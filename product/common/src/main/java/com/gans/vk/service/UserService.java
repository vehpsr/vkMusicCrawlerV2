package com.gans.vk.service;

import java.util.List;
import java.util.Map.Entry;

import com.gans.vk.dao.UserDao.UserLibData;
import com.gans.vk.json.StatNode;
import com.gans.vk.model.impl.User;

public interface UserService {

    User get(long id);
    User getByVkId(String vkId);
    User getByUrl(String url);
    void save(User user);
    List<UserLibData> getRecomendedUserLibData(User user, User target);
    void importUnique(List<Entry<String, String>> users);
    int getUndiscoveredUsersCount();
    List<User> getUndiscoveredUsers(int limit);
    User getRandomUser();
    StatNode statisticsUserData();
    void resolve(long userId);

}
