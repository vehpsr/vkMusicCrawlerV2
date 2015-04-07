package com.gans.vk.dao.impl;

import com.gans.vk.dao.AbstractModelDao;
import com.gans.vk.dao.SongDao;
import com.gans.vk.model.impl.Song;

public class SongDaoImpl extends AbstractModelDao<Song> implements SongDao {

    public SongDaoImpl() {
        super(Song.class);
    }

}
