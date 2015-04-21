package com.gans.vk.dao.impl;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.GroupDao;
import com.gans.vk.model.impl.Group;

public class GroupDaoImpl extends AbstractModelDao<Group> implements GroupDao {

    public GroupDaoImpl() {
        super(Group.class);
    }

}
