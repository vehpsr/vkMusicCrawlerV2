package com.gans.vk.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.UserDao;
import com.gans.vk.model.impl.User;

public class UserDaoImpl extends AbstractModelDao<User> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public User getUserByUrl(String url) {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.eq("url", url));
        return findUnique(criteria);
    }

    @Override
    public User getUserByVkId(String vkId) {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.eq("vkId", vkId));
        return findUnique(criteria);
    }

}
