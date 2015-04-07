package com.gans.vk.dao.impl;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.RatingDao;
import com.gans.vk.model.impl.Rating;

public class RatingDaoImpl extends AbstractModelDao<Rating> implements RatingDao {

    public RatingDaoImpl() {
        super(Rating.class);
    }

}
