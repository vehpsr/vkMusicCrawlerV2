package com.gans.vk.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.UserDao;
import com.gans.vk.dao.UserDao.UserLibData;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.UserService;

public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao _userDao;

    public void setUserDao(UserDao userDao) {
        _userDao = userDao;
    }

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

}
