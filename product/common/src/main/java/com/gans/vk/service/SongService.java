package com.gans.vk.service;

import java.util.List;

import com.gans.vk.dao.SongDao.SongData;
import com.gans.vk.json.StatNode;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface SongService {

    List<SongData> getAllUnratedSongs(User target, User user, int limit);
    Song get(long id);
    StatNode statisticsSongData(User user);
}
