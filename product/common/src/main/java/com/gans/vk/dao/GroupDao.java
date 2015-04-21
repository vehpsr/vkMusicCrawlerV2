package com.gans.vk.dao;

import com.gans.vk.model.impl.Group;

public interface GroupDao extends ModelDao<Group> {

    Group getByUrl(String vkUrl);

}
