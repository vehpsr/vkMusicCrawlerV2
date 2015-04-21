package com.gans.vk.service;

import com.gans.vk.model.impl.Group;

public interface GroupService {

    Group gerByUrl(String vkUrl);
    void save(Group group);

}
