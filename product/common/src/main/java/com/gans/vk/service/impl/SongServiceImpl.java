package com.gans.vk.service.impl;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.SongDao;
import com.gans.vk.dao.SongDao.SongData;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.SongService;

public class SongServiceImpl implements SongService {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(SongServiceImpl.class);

    @Autowired
    private SongDao _songDao;

    @Override
    public List<SongData> getAllUnratedSongs(User target, User user, int limit) {
        return _songDao.getAllUnratedSongs(target, user, limit);
    }

    @Override
    public Song get(long id) {
        return _songDao.get(id);
    }

    @Override
    public List<Entry<String, Integer>> statisticsSongData() {
        return Collections.<Entry<String, Integer>>singletonList(new AbstractMap.SimpleEntry<String, Integer>("Total Songs", _songDao.countAll()));
    }

    public void setSongDao(SongDao songDao) {
        _songDao = songDao;
    }

}
