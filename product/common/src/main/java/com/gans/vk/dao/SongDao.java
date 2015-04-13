package com.gans.vk.dao;

import java.util.List;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface SongDao extends ModelDao<Song> {

    List<Song> getAllUnratedSongs(User target, User user, int limit);

}
