package com.gans.vk.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.RatingDao;
import com.gans.vk.model.impl.Rating;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public class RatingDaoImpl extends AbstractModelDao<Rating> implements RatingDao {

    public RatingDaoImpl() {
        super(Rating.class);
    }

    @Override
    public Rating getBy(User user, Song song) {
        DetachedCriteria criteria = createCriteria();
        criteria.add(Restrictions.eq("user", user));
        criteria.add(Restrictions.eq("song", song));
        return findUnique(criteria);
    }

}
