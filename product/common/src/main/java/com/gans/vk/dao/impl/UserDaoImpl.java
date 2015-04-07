package com.gans.vk.dao.impl;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.UserDao;
import com.gans.vk.model.impl.User;

public class UserDaoImpl extends AbstractModelDao<User> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

}
