package com.gans.vk.service;

import java.util.List;

import com.gans.vk.dao.UserDao.UserLibData;
import com.gans.vk.model.impl.User;

public interface UserService {

    User getByVkId(String vkId);
    User getByUrl(String url);
    void save(User user);
    List<UserLibData> getRecomendedAudioLibsFor(User user);

}
