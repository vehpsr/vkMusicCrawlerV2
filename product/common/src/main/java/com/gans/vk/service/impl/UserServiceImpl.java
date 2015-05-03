package com.gans.vk.service.impl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.UserDao;
import com.gans.vk.dao.UserDao.UserLibData;
import com.gans.vk.model.impl.User;
import com.gans.vk.model.impl.User.UserStatus;
import com.gans.vk.service.UserService;

public class UserServiceImpl implements UserService {

    private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);

    @Autowired private UserDao _userDao;

    @Override
    public User getByVkId(String vkId) {
        return _userDao.getUserByVkId(vkId);
    }

    @Override
    public User getByUrl(String url) {
        return _userDao.getUserByUrl(url);
    }

    @Override
    public void save(User user) {
        _userDao.save(user);
    }

    @Override
    public List<UserLibData> getRecomendedAudioLibsFor(User user) {
        return _userDao.getRecomendedAudioLibsFor(user);
    }

    @Override
    public void importUnique(List<Entry<String, String>> users) {
        _userDao.importUnique(users);
    }

    @Override
    public int getUndiscoveredUsersCount() {
        return _userDao.getUndiscoveredUsersCount();
    }

    @Override
    public List<User> getUndiscoveredUsers(int limit) {
        return _userDao.getUndiscoveredUsers(limit);
    }

    @Override
    public User get(long id) {
        return _userDao.get(id);
    }

    @Override
    public User getRandomUser() {
        return _userDao.getRandomUser();
    }

    @Override
    public List<Entry<String, Integer>> statisticsUserData() {
        int total = _userDao.countAll();
        int undiscovered = _userDao.getUndiscoveredUsersCount();
        List<Entry<String, Integer>> userStatusStatistics = _userDao.userStatusStatistics();

        List<Entry<String, Integer>> result = new ArrayList<>();
        result.add(new AbstractMap.SimpleEntry<String, Integer>("Total Users", total));
        result.add(new AbstractMap.SimpleEntry<String, Integer>("Undiscovered", undiscovered));
        result.add(new AbstractMap.SimpleEntry<String, Integer>("Discovered", total - undiscovered));
        for (Entry<String, Integer> userStatus : userStatusStatistics) {
            result.add(userStatus);
        }
        return result;
    }

    @Override
    public void resolve(long userId) {
        User user = get(userId);
        if (user == null) {
            LOG.error("Fail to find user with Id: " + userId);
            return;
        }
        user.setVkId(UserStatus.RESOLVED.name());
        _userDao.save(user);
    }

    public void setUserDao(UserDao userDao) {
        _userDao = userDao;
    }
}
