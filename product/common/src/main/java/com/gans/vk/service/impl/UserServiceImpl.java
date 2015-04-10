package com.gans.vk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.UserDao;
import com.gans.vk.service.UserService;

public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao _userDao;

    public void setUserDao(UserDao userDao) {
        _userDao = userDao;
    }

}