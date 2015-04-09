package com.gans.vk.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.SongDao;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.SongService;

public class SongServiceImpl implements SongService {

    @Autowired
    private SongDao _songDao;

    @Override
    public List<Song> getAllUnratedSongs(User user, int limit) {
        return _songDao.getAllUnratedSongs(user, limit);
    }

    @Override
    public Song get(long id) {
        return _songDao.get(id);
    }

    public void setSongDao(SongDao songDao) {
        _songDao = songDao;
    }

}
