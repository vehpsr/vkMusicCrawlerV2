package com.gans.vk.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.SongDao;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.SongService;

public class SongServiceImpl implements SongService {

    private static final Log LOG = LogFactory.getLog(SongServiceImpl.class);

    @Autowired
    private SongDao _songDao;

    @Override
    public List<Song> getAllUnratedSongs(User target, User user, int limit) {
        List<Song> unratedSongs = _songDao.getAllUnratedSongs(target, user, limit);
        if (unratedSongs.isEmpty()) {
            LOG.info("Fail to find any unrated songs");
            return Collections.emptyList();
        }
        return unratedSongs;
    }

    @Override
    public Song get(long id) {
        return _songDao.get(id);
    }

    public void setSongDao(SongDao songDao) {
        _songDao = songDao;
    }

}
