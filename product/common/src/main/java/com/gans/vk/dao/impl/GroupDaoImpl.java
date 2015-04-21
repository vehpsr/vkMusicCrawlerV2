package com.gans.vk.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.GroupDao;
import com.gans.vk.model.impl.Group;

public class GroupDaoImpl extends AbstractModelDao<Group> implements GroupDao {

    public GroupDaoImpl() {
        super(Group.class);
    }

    @Override
    public Group getByUrl(String url) {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.eq("url", url));
        return findUnique(criteria);
    }

}
