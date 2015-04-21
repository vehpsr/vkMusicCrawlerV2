package com.gans.vk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.GroupDao;
import com.gans.vk.model.impl.Group;
import com.gans.vk.service.GroupService;

public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao _groupDao;

    public void setGroupDao(GroupDao groupDao) {
        _groupDao = groupDao;
    }

    @Override
    public Group gerByUrl(String vkUrl) {
        return _groupDao.getByUrl(vkUrl);
    }

    @Override
    public void save(Group group) {
        _groupDao.save(group);
    }

}
