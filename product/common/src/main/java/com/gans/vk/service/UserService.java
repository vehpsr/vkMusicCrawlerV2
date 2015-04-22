package com.gans.vk.service;

import java.util.List;
import java.util.Map.Entry;

import com.gans.vk.dao.UserDao.UserLibData;
import com.gans.vk.model.impl.User;

public interface UserService {

    User getByVkId(String vkId);
    User getByUrl(String url);
    void save(User user);
    List<UserLibData> getRecomendedAudioLibsFor(User user);
    void importUnique(List<Entry<String, String>> users);

}
