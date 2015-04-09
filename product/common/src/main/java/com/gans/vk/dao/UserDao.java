package com.gans.vk.dao;

import com.gans.vk.model.impl.User;

public interface UserDao extends ModelDao<User> {

    User getUserByUrl(String name);

}
