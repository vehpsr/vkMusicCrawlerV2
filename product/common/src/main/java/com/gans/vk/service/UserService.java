package com.gans.vk.service;

import com.gans.vk.model.impl.User;

public interface UserService {

    User getByVkId(String vkId);

}
